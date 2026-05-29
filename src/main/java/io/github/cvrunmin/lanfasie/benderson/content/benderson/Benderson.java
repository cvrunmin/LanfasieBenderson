package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllAttributes;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityDataSerializers;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Benderson extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> ANIMATE_STATE = SynchedEntityData.defineId(Benderson.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Optional<HashMap<UUID, Float>>> ENMITY_SYNCER = SynchedEntityData.defineId(Benderson.class, AllEntityDataSerializers.OPTIONAL_UUID_FLOAT_MAP.get());
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> TARGET_SYNCER = SynchedEntityData.defineId(Benderson.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private DamageSource lastDamageSource;
    private long lastDamageStamp;

    protected HashMap<UUID, Float> enmityList = new HashMap<>();

    private IdlePhaseState idlePhaseState = new IdlePhaseState(this);
    private NormalAttackPhaseState normalAttackPhaseState = new NormalAttackPhaseState(this);
    private LethalAttackPhaseState lethalAttackPhaseState = new LethalAttackPhaseState(this);
    private CircleAoeSelfPhaseState circleAoeSelfPhaseState = new CircleAoeSelfPhaseState(this, 22);
    private CircleStackAttackPhaseState circleStackAttackPhaseState = new CircleStackAttackPhaseState(this, 20);
    private PartialArenaAoePhaseState threeforthAreanAoePhaseState = new PartialArenaAoePhaseState(this, 22);
    private SummonAnticalabrumPhaseState summonAnticalabrumPhaseState = new SummonAnticalabrumPhaseState(this);
    private int globalCooldown;
    private PhaseStateTransitioner transitioner;

    private boolean shouldChangePhase = false;
    private ServerBossEvent bossEvent = Util.make(
            new ServerBossEvent(this.uuid, this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS),
            e -> e.setDarkenScreen(true)
    );

    private TargetMarker arenaHintMarker;
    private BlockPos arenaCenter;
    private int arenaRadius = 24;
    private final DamageGate damageGate = new DamageGate(20);

    public Benderson(EntityType<? extends Benderson> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 180, true);
        transitioner = new PhaseStateTransitioner(this);
        transitioner.addPhaseStateInstance("idle", idlePhaseState)
                .addPhaseStateInstance("attack", normalAttackPhaseState)
                .addPhaseStateInstance("lethal_attack", lethalAttackPhaseState)
                .addPhaseStateInstance("circle_aoe_self", circleAoeSelfPhaseState)
                .addPhaseStateInstance("circle_stack", circleStackAttackPhaseState)
                .addPhaseStateInstance("three-fourth_arena_aoe", threeforthAreanAoePhaseState)
                .addPhaseStateInstance("summon_anticalabrum", summonAnticalabrumPhaseState)
                .addTransition("idle", "idle", 0)
                .addTransition("idle", "summon_anticalabrum")
                .addTransition("idle", "attack")
                .addTransition("attack", "idle", -1)
                .addTransition("attack", "attack", 0)
                .addTransition("attack", "summon_anticalabrum", 10)
                .addTransition("attack", "lethal_attack")
                .addTransition("attack", "circle_aoe_self")
                .addTransition("attack", "circle_stack")
                .addTransition("attack", "three-fourth_arena_aoe")
                .addTransition("lethal_attack", "idle", 0)
                .addTransition("lethal_attack", "attack")
                .addTransition("circle_aoe_self", "idle", 0)
                .addTransition("circle_aoe_self", "attack")
                .addTransition("circle_stack", "idle", 0)
                .addTransition("circle_stack", "attack")
                .addTransition("three-fourth_arena_aoe", "idle", 0)
                .addTransition("three-fourth_arena_aoe", "attack")
                .addTransition("summon_anticalabrum", "idle", 0)
                .addTransition("summon_anticalabrum", "attack");
    }

    public Benderson(Level level, double x, double y, double z) {
        this(AllEntityTypes.BENDERSON.get(), level);
        this.setPos(x, y, z);
        this.arenaCenter = BlockPos.containing(x, y, z);
        this.arenaHintMarker = TargetMarker.byBlockPosLowerCorner(level, this.arenaCenter, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.ARENA_HINT, arenaRadius, 1));
        this.arenaHintMarker.setPersistent(true);
        level.addFreshEntity(this.arenaHintMarker);
    }

    private void ensureBossEventUidMatch(){
        var oldBossEvent = bossEvent;
        var players = new ArrayList<>(oldBossEvent.getPlayers());
        oldBossEvent.removeAllPlayers();
        bossEvent = Util.make(
                new ServerBossEvent(this.uuid, this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS),
                e -> e.setDarkenScreen(true)
        );
        for (ServerPlayer player : players) {
            bossEvent.addPlayer(player);
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if(!level().isClientSide()) {
            if(this.arenaCenter == null) {
                this.arenaCenter = blockPosition();
            }
            if(this.arenaHintMarker == null){
                this.arenaHintMarker = TargetMarker.byBlockPosLowerCorner(level(), this.arenaCenter, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.ARENA_HINT, arenaRadius, 1));
                this.arenaHintMarker.setPersistent(true);
                level().addFreshEntity(this.arenaHintMarker);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ANIMATE_STATE, "idle");
        entityData.define(ENMITY_SYNCER, Optional.empty());
        entityData.define(TARGET_SYNCER, Optional.empty());
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)1000.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.CAMERA_DISTANCE, (double)16.0F)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(),
                new AnimationController<>("Attack", test -> {
                    if (test.getDataOrDefault(DataTickets.SWINGING_ARM, false))
                        return test.setAndContinue(DefaultAnimations.ATTACK_SWING);
                    return test.setAndContinue(DefaultAnimations.IDLE);
//                    return PlayState.STOP;
                }),
                new AnimationController<>("Special Attack", test -> {
                    var animateState = test.getDataOrDefault(BendersonDataTickets.ANIMATE_STATE, "");
                    return switch (animateState) {
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.strong_attack.start"));
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.strong_attack.end"));
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_LOOP,
                             CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_LOOP,
                             CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_LOOP,
                             PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP -> PlayState.CONTINUE;
                        case CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.circular_swing.start"));
                        case CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.circular_swing.end"));
                        case CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.magic.start"));
                        case CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.magic.end"));
                        case PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.facing_swing.start"));
                        case PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.facing_swing.end"));
                        case SummonAnticalabrumPhaseState.ANIMATE_STATE_START ->
                            test.setAndContinue(RawAnimation.begin().thenPlay("cast.sword"));
                        default -> PlayState.STOP;
                    };
                }));
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new TopEnmityTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestTargetGoal(this, 6));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if(level().isClientSide() && accessor == ENMITY_SYNCER){
            this.enmityList = entityData.get(ENMITY_SYNCER).orElse(new HashMap<>());
        }
    }

    @Override
    public void tick() {
        if(!level().isClientSide()){
            damageGate.tick();
        }
        super.tick();
    }

    protected void progressPhaseState(){
        if(this.level().isClientSide()) return;
        transitioner.tick();
        shouldChangePhase = transitioner.isShouldChangePhase();
    }

    public void setPhaseState(String state){
        this.transitioner.setPhaseState(state);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if(globalCooldown > 0) --globalCooldown;
        if((this.tickCount + this.getId()) % 2 != 0 && this.tickCount > 1 && shouldChangePhase){
            // force refresh targetGoal
            this.targetSelector.tick();
        }
        progressPhaseState();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if(tickCount % 20 == 0){
            this.entityData.set(ENMITY_SYNCER, Optional.ofNullable(getEnmityMapForSyncing()));
        }
    }

    public void setGlobalCooldown(int value){
        globalCooldown = Math.max(value, 0);
    }

    public boolean isInGlobalCooldown(){
        return globalCooldown > 0;
    }

    public String getAnimateState(){
        return this.entityData.get(ANIMATE_STATE);
    }

    public void setAnimateState(String state){
        this.entityData.set(ANIMATE_STATE, state);
    }

    @Override
    public void setTarget(@org.jspecify.annotations.Nullable LivingEntity target) {
        super.setTarget(target);
        this.entityData.set(TARGET_SYNCER, Optional.ofNullable(EntityReference.of(target)));
    }

    public boolean isPlayerTargeted(UUID uuid){
        return this.entityData.get(TARGET_SYNCER).map(ref -> EntityReference.getLivingEntity(ref, level()))
                .map(Entity::getUUID)
                .map(u1 -> u1.equals(uuid))
                .orElse(false);
    }

    @Override
    public Component getName() {
        if(this.hasCustomName()) return super.getName();
        return Component.translatable("entity.lanfasie_benderson.benderson.name.deep_latent");
    }

    @Override
    public boolean canUsePortal(boolean ignorePassenger) {
        return false;
    }

    @Override
    public boolean isAffectedByFluids() {
        return false;
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        Entity maybeHint = input.read("ArenaHint", UUIDUtil.CODEC).map(uuid -> level().getEntity(uuid)).orElse(null);
        if(maybeHint instanceof TargetMarker arenaHint){
            this.arenaHintMarker = arenaHint;
        }
        this.enmityList = new HashMap<>();
        var maybeEnmityListObj = input.childrenList("Enmity");
        if(maybeEnmityListObj.isPresent()){
            for (ValueInput child : maybeEnmityListObj.get()) {
                var maybeUuid = child.read("UUID", UUIDUtil.CODEC);
                maybeUuid.ifPresent(value -> this.enmityList.put(value, child.getFloatOr("Value", 0f)));
            }
        }
        transitioner.readAdditionalSaveData(input);
        input.read("ArenaCenter", BlockPos.CODEC).ifPresent(pos -> {
            this.arenaCenter = pos;
            if (!level().isClientSide()) {
                if (this.arenaHintMarker == null) {
                    this.arenaHintMarker = TargetMarker.byBlockPosLowerCorner(level(), this.arenaCenter, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.ARENA_HINT, arenaRadius, 1));
                    this.arenaHintMarker.setPersistent(true);
                    level().addFreshEntity(this.arenaHintMarker);
                }else{
                    this.arenaHintMarker.setTargetPos(this.arenaCenter);
                }
            }
        });
        setAnimateState(input.getStringOr("AnimateState", "idle"));
        globalCooldown = input.getIntOr("GCD", 0);
        ensureBossEventUidMatch();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if(this.arenaHintMarker != null) {
            output.store("ArenaHint", UUIDUtil.CODEC, this.arenaHintMarker.getUUID());
        }
        var enmityListObj = output.childrenList("Enmity");
        for (Map.Entry<UUID, Float> entry : this.enmityList.entrySet()) {
            var child = enmityListObj.addChild();
            child.store("UUID", UUIDUtil.CODEC, entry.getKey());
            child.putFloat("Value", entry.getValue());
        }
        transitioner.addAdditionalSaveData(output);
        if(this.arenaCenter != null) {
            output.store("ArenaCenter", BlockPos.CODEC, this.arenaCenter);
        }
        output.putString("AnimateState", getAnimateState());
        output.putInt("GCD", globalCooldown);
    }

    @Override
    public void onRemoval(RemovalReason reason) {
        super.onRemoval(reason);
        if(arenaHintMarker != null) {
            arenaHintMarker.discard();
        }
    }

    @Override
    public int getCurrentSwingDuration() {
        return 17;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        float dmg = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = this.damageSources().source(AllDamageTypes.BOSS_NORMAL_ATTACK, this);
        boolean wasHurt = target.hurtServer(level, damageSource, dmg);
        if (wasHurt) {
            this.setLastHurtMob(target);
            this.playAttackSound();
        }
        return wasHurt;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        } else if (this.isDeadOrDying()) {
            return false;
        } else if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        this.damageContainers.push(new DamageContainer(source, damage));
        if (CommonHooks.onEntityIncomingDamage(this, this.damageContainers.peek())) return false;
        if (this.isSleeping()) {
            this.stopSleeping();
        }

        this.noActionTime = 0;
        damage = this.damageContainers.peek().getNewDamage(); //Neo: enforce damage container as source of truth for damage amount
        if (damage < 0.0F) {
            damage = 0.0F;
        }
        if (Float.isNaN(damage) || Float.isInfinite(damage)) {
            damage = Float.MAX_VALUE;
        }
        var inputDamage = damage;
        damage = Math.min(damage, this.getMaxHealth() * 0.01f);
        var totalDamageInGate = damageGate.getTotalDamage();
        var timegatedDamage = this.getMaxHealth() * 0.01f;
        if(totalDamageInGate + damage > timegatedDamage){
            damage = Math.max(0, timegatedDamage - totalDamageInGate);
        }
        if(damage > 0){
            this.actuallyHurt(level, source, damage);
            this.lastHurt = damage;
            this.damageGate.addRecord(damage);
        }
        this.resolveMobResponsibleForDamage(source);
        this.resolvePlayerResponsibleForDamage(source);
        level.broadcastDamageEvent(this, source);
        if (!source.is(DamageTypeTags.NO_IMPACT) && (damage > 0.0F)) {
            this.markHurt();
        }
        if (this.isDeadOrDying()) {
            this.makeSound(this.getDeathSound());
            this.die(source);
        } else {
            this.playHurtSound(source);
        }

        boolean success = damage > 0.0F;
        if (success) {
            this.lastDamageSource = source;
            this.lastDamageStamp = this.level().getGameTime();

            for (MobEffectInstance effect : this.getActiveEffects()) {
                effect.onMobHurt(level, this, source, damage);
            }
        }

        if (source.getEntity() instanceof ServerPlayer sourcePlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(sourcePlayer, this, source, damage, damage, false);
            this.enmityList.putIfAbsent(sourcePlayer.getUUID(), 0f);
            var enmity = inputDamage * sourcePlayer.getAttributeValue(AllAttributes.ENMITY_MULTIPLIER);
            this.enmityList.merge(sourcePlayer.getUUID(), (float) enmity, Float::sum);
        }

        this.damageContainers.pop();
        return success;
    }

    @Override
    public void die(DamageSource source) {
        if(arenaHintMarker != null && arenaHintMarker.isAlive()){
            arenaHintMarker.discard();
        }
        super.die(source);
    }

    @Override
    public void handleDamageEvent(DamageSource source) {
        this.invulnerableTime = 20;
        this.hurtDuration = 10;
        this.hurtTime = this.hurtDuration;
        SoundEvent hurtSound = this.getHurtSound(source);
        if (hurtSound != null) {
            this.playSound(hurtSound, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
        this.lastDamageSource = source;
        this.lastDamageStamp = this.level().getGameTime();
    }

    public @Nullable DamageSource getLastDamageSource() {
        if (this.level().getGameTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }

        return this.lastDamageSource;
    }

    @Override
    public void knockback(double power, double xd, double zd) {

    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    public Vec3 getCombatArenaCenter(){
        if(this.arenaCenter == null) return Vec3.ZERO;
        return Vec3.atLowerCornerOf(this.arenaCenter);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target.canBeSeenAsEnemy();
    }

    public Map<Player, Float> getActualEnmityMap(){
        var arena = getCombatArena();
        return this.enmityList.entrySet().stream().map(entry -> {
            var entity = this.level().getEntity(entry.getKey());
            if(!(entity instanceof Player player)) return null;
            if(!player.isAlive()) return null;
            if(arena.contains(player.position())) return Map.entry(player, entry.getValue());
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private HashMap<UUID, Float> getEnmityMapForSyncing(){
        var arena = getCombatArena();
        return this.enmityList.entrySet().stream().filter(entry -> {
            var entity = this.level().getEntity(entry.getKey());
            if(!(entity instanceof Player player)) return false;
            if(!player.isAlive()) return false;
            return arena.contains(player.position());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, HashMap::new));
    }

    public record EnmityBarInfo(int rank, float barPercentage){}

    public EnmityBarInfo getEnmityBarInfo(UUID player){
        if(!enmityList.containsKey(player)) return new EnmityBarInfo(-1, 1);
        var sortedPlayerList = enmityList.entrySet().stream().sorted(Map.Entry.<UUID, Float>comparingByValue().reversed()).map(Map.Entry::getKey).toList();
        var i = sortedPlayerList.indexOf(player);
        if(i == -1) return new EnmityBarInfo(-1, 1);
        var enmity = enmityList.get(player);
        var maxEnmity = enmityList.get(sortedPlayerList.getFirst());
        return new EnmityBarInfo(i + 1, Mth.clamp(enmity / Math.max(0.0001f, maxEnmity), 0, 1));
    }

    public @NonNull AABB getCombatArena() {
        if(this.arenaCenter == null) return new AABB(-32, -5, -32, 32, 5, 32);
        return AABB.ofSize(Vec3.atLowerCornerOf(this.arenaCenter), this.arenaRadius * 2, 10, this.arenaRadius * 2);
    }

    public int getArenaRadius(){
        return this.arenaRadius;
    }

    public static class TopEnmityTargetGoal extends Goal{

        protected final Benderson owner;
        protected @Nullable Player target;

        public TopEnmityTargetGoal(Benderson owner){
            this.owner = owner;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if(!this.owner.shouldChangePhase) return false;
            findTarget();
            return this.target != null;
        }

        protected void findTarget(){
            var enmityMap = this.owner.getActualEnmityMap();
            this.target = enmityMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        }

        @Override
        public void start() {
            this.owner.setTarget(this.target);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.owner.shouldChangePhase;
        }

        @Override
        public void stop() {
            this.owner.setTarget(null);
            this.target = null;
        }
    }

    public static class NearestTargetGoal extends Goal{
        private final Benderson owner;
        private final float range;
        protected @Nullable Player target;

        public NearestTargetGoal(Benderson owner){
            this(owner, owner.arenaRadius);
        }

        public NearestTargetGoal(Benderson owner, float range) {
            this.owner = owner;
            this.range = range;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if(!this.owner.shouldChangePhase) return false;
            findTarget();
            return this.target != null;
        }

        protected void findTarget() {
            ServerLevel level = getServerLevel(this.owner);
            this.target = level.getNearbyPlayers(TargetingConditions.forCombat(), this.owner, this.owner.getCombatArena())
                    .stream().map(player -> new Tuple<>(player, this.owner.position().distanceTo(player.position())))
                    .filter(tuple -> tuple.getB() <= this.range)
                    .min(Comparator.comparing(Tuple::getB))
                    .map(Tuple::getA)
                    .orElse(null);
        }

        @Override
        public void start() {
            this.owner.setTarget(this.target);
            this.owner.enmityList.putIfAbsent(this.target.getUUID(), 0f);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.owner.shouldChangePhase;
        }

        @Override
        public void stop() {
            this.owner.setTarget(null);
            this.target = null;
        }
    }

    public static class DamageGate{
        private final int maxRecordingDeltaTime;
        private Deque<Float> damageDeque = new ArrayDeque<>();
        private Deque<Integer> damageTimeDeque = new ArrayDeque<>();

        public DamageGate(int maxRecordingDeltaTime){
            this.maxRecordingDeltaTime = maxRecordingDeltaTime;
        }

        private void clearOutdatedRecord(){
            while (!damageTimeDeque.isEmpty()){
                if(damageTimeDeque.peekFirst() <= 0){
                    damageTimeDeque.pollFirst();
                    damageDeque.pollFirst();
                }else{
                    break;
                }
            }
        }

        public void tick(){
            for (int i = 0; i < damageTimeDeque.size(); i++) {
                damageTimeDeque.add(damageTimeDeque.pop() - 1);
            }
        }

        public void addRecord(float damage){
            clearOutdatedRecord();
            damageDeque.add(damage);
            damageTimeDeque.add(maxRecordingDeltaTime);
        }

        public float getTotalDamage(){
            clearOutdatedRecord();
            return (float) damageDeque.stream().mapToDouble(v -> v).sum();
        }
    }
}
