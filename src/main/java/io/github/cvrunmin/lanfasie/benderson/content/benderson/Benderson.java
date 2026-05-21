package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.util.Util;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Benderson extends Monster implements GeoEntity {
    private static final EntityDataAccessor<String> ANIMATE_STATE = SynchedEntityData.defineId(Benderson.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private DamageSource lastDamageSource;
    private long lastDamageStamp;

    protected HashMap<UUID, Float> enmityList = new HashMap<>();

    private NormalAttackPhaseState normalAttackPhaseState = new NormalAttackPhaseState(this);
    private LethalAttackPhaseState lethalAttackPhaseState = new LethalAttackPhaseState(this);

    private Map<String, IPhaseState> possiblePhaseStates = Map.ofEntries(
            Map.entry("attack", normalAttackPhaseState),
            Map.entry("lethal_attack", lethalAttackPhaseState)
    );
    private IPhaseState currentPhaseState;
    private boolean shouldChangePhase = false;
    private final ServerBossEvent bossEvent = Util.make(
            new ServerBossEvent(this.uuid, this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS),
            e -> e.setDarkenScreen(true)
    );

    public Benderson(EntityType<? extends Benderson> type, Level level) {
        super(type, level);
    }

    public Benderson(Level level, double x, double y, double z) {
        this(AllEntityTypes.BENDERSON.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ANIMATE_STATE, "idle");
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)1000.0F)
                .add(Attributes.CAMERA_DISTANCE, (double)16.0F)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(),
                new AnimationController<>("Attack", test -> {
                    if (test.getDataOrDefault(DataTickets.SWINGING_ARM, false))
                        return test.setAndContinue(DefaultAnimations.ATTACK_SWING);
                    test.controller().reset();
                    return PlayState.STOP;
                }));
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    protected void progressPhaseState(){
        if(this.level().isClientSide()) return;
        if(currentPhaseState == null) {
            currentPhaseState = normalAttackPhaseState;
            currentPhaseState.start();
        }else if(shouldChangePhase){
            currentPhaseState = normalAttackPhaseState;
            currentPhaseState.start();
            shouldChangePhase = false;
        } else {
            if(!currentPhaseState.tick()){
                currentPhaseState.end();
                shouldChangePhase = true;
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        progressPhaseState();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public String getAnimateState(){
        return this.entityData.get(ANIMATE_STATE);
    }

    public void setAnimateState(String state){
        this.entityData.set(ANIMATE_STATE, state);
    }

    @Override
    public Component getName() {
        if(this.hasCustomName()) return super.getName();
        return Component.translatable("entity.lanfasie_benderson.benderson.name.deep_latent");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.enmityList = new HashMap<>();
        var maybeEnmityListObj = input.childrenList("Enmity");
        if(maybeEnmityListObj.isPresent()){
            for (ValueInput child : maybeEnmityListObj.get()) {
                var maybeUuid = child.read("UUID", UUIDUtil.CODEC);
                maybeUuid.ifPresent(value -> this.enmityList.put(value, child.getFloatOr("Value", 0f)));
            }
        }
        var maybeCurrentPhase = input.getStringOr("Phase", "");
        var maybePhaseData = input.childOrEmpty("PhaseData");
        for (Map.Entry<String, IPhaseState> entry : this.possiblePhaseStates.entrySet()) {
            entry.getValue().readAdditionalSaveData(maybePhaseData.childOrEmpty(entry.getKey()));
            if(maybeCurrentPhase.equals(entry.getKey())){
                this.currentPhaseState = entry.getValue();
            }
        }
        setAnimateState(input.getStringOr("AnimateState", "idle"));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        var enmityListObj = output.childrenList("Enmity");
        for (Map.Entry<UUID, Float> entry : this.enmityList.entrySet()) {
            var child = enmityListObj.addChild();
            child.store("UUID", UUIDUtil.CODEC, entry.getKey());
            child.putFloat("Value", entry.getValue());
        }
        var phasesObj = output.child("PhaseData");
        for (Map.Entry<String, IPhaseState> entry : this.possiblePhaseStates.entrySet()) {
            if(currentPhaseState == entry.getValue()){
                output.putString("Phase", entry.getKey());
            }
            entry.getValue().addAdditionalSaveData(phasesObj.child(entry.getKey()));
        }
        output.putString("AnimateState", getAnimateState());
    }

    @Override
    public int getCurrentSwingDuration() {
        return 17;
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
        this.damageContainers.push(new net.neoforged.neoforge.common.damagesource.DamageContainer(source, damage));
        if (net.neoforged.neoforge.common.CommonHooks.onEntityIncomingDamage(this, this.damageContainers.peek())) return false;
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
        this.actuallyHurt(level, source, damage);
        this.lastHurt = damage;
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
            this.enmityList.merge(sourcePlayer.getUUID(), damage, Float::sum);
        }

        this.damageContainers.pop();
        return success;
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
}
