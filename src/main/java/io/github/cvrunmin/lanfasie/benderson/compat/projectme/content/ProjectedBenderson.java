package io.github.cvrunmin.lanfasie.benderson.compat.projectme.content;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonDataTickets;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonStatesGetter;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.*;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.content.particles.BlockParticleDustEmitterOption;
import io.github.cvrunmin.lanfasie.benderson.content.particles.ColoredDustEmitterOption;
import io.github.cvrunmin.lanfasie.benderson.index.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ProjectedBenderson extends LivingEntity implements GeoEntity, BendersonStatesGetter {
    private static final EntityDataAccessor<String> ANIMATE_STATE = SynchedEntityData.defineId(ProjectedBenderson.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Optional<HashMap<UUID, Float>>> ENMITY_SYNCER = SynchedEntityData.defineId(ProjectedBenderson.class, AllEntityDataSerializers.OPTIONAL_UUID_FLOAT_MAP.get());
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> TARGET_SYNCER = SynchedEntityData.defineId(ProjectedBenderson.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Benderson.BodyState> BODY_STATE = SynchedEntityData.defineId(ProjectedBenderson.class, AllEntityDataSerializers.BENDERSON_BODY_STATE.get());
    private static final EntityDataAccessor<Integer> ARENA_RADIUS = SynchedEntityData.defineId(ProjectedBenderson.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> ARENA_CENTER = SynchedEntityData.defineId(ProjectedBenderson.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> SHOULD_HIDE_BOUNDING_BOX = SynchedEntityData.defineId(ProjectedBenderson.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int phaseStateTick;
    private String currentPhaseState = "";
    private Anticalabrum.AnticalabrumType anticalabrumNextType;
    private long anticalabrumNextSeed;


    public ProjectedBenderson(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public ProjectedBenderson(Level level) {
        this(AllEntityTypes.PROJECTED_BENDERSON.get(), level);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ANIMATE_STATE, "idle");
        entityData.define(ENMITY_SYNCER, Optional.empty());
        entityData.define(TARGET_SYNCER, Optional.empty());
        entityData.define(BODY_STATE, Benderson.BodyState.DEEP_LATENT);
        entityData.define(ARENA_CENTER, Optional.empty());
        entityData.define(ARENA_RADIUS, 24);
        entityData.define(SHOULD_HIDE_BOUNDING_BOX, false);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return true;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()){
            this.phaseStateTick++;
            handlePhaseTick();
        }
    }

    public void setPhaseState(String phaseId, CompoundTag extraData){
        this.phaseStateTick = 0;
        this.currentPhaseState = phaseId;
        handlePhaseStart(extraData);
    }

    protected void handlePhaseStart(CompoundTag extraData){
        switch (this.currentPhaseState){
            case "arena_entering":
                this.setBodyState(Benderson.BodyState.ENTRANCE);
                this.setAnimateState(ArenaEnteringPhaseState.ANIMATE_STATE_START);
                break;
            case "idle":
                this.setAnimateState("idle");
                break;
            case "lethal_attack":
                this.setAnimateState(LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_START);
                break;
            case "circle_aoe_self":
                var marker = new TargetMarker(level(), this,
                        TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_AOE, 10, 110));
                level().addFreshEntity(marker);
                this.setAnimateState(CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_START);
                break;
            case "circle_stack":
                this.setAnimateState(CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_START);
                break;
            case "three-fourth_arena_aoe":
                break;
            case "summon_anticalabrum":
                this.setAnimateState(SummonAnticalabrumPhaseState.ANIMATE_STATE_START);
                this.anticalabrumNextType = extraData.getInt("NextType").map(i -> Anticalabrum.AnticalabrumType.values()[i]).orElse(Anticalabrum.AnticalabrumType.EMPTY);
                this.anticalabrumNextSeed = extraData.getLongOr("Seed", 0);
                break;
            case "elevate_to_extreme":
                this.setBodyState(Benderson.BodyState.TRANSITION_UNFORGIVEN);
                this.setAnimateState(ElevateToExtremeState.ANIMATE_STATE_P1);
                break;
            case "knockback_from_center":
                this.setAnimateState(KnockbackFromCenterPhaseState.ANIMATE_STATE_START);
                break;
            case "summon_blocking_pile":
                this.setAnimateState(PreEclipticMeteorState.ANIMATE_STATE_START);
                break;
            case "ecliptic_meteor":
                this.setAnimateState(EclipticMeteorState.ANIMATE_STATE_START);
                break;
        }
    }

    protected void handlePhaseTick(){
        switch (this.currentPhaseState){
            case "arena_entering":
                if(phaseStateTick >= 105 && phaseStateTick <= 120){
                    var zOffset = ((phaseStateTick - 105) / 15f * 2 - 1) * this.getArenaRadius();
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, getX() + zOffset, getY() + 0.5, getZ(), getArenaRadius() * 2, 0, 0, getArenaRadius() * 0.75, 0);
                    level().playSound(null, getX() + zOffset, getY() + 0.5, getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1f, 0.75f);
                }
                if(phaseStateTick == 155){
                    this.setBodyState(Benderson.BodyState.DEEP_LATENT);
                    this.setAnimateState("idle");
                    ((ServerLevel) level()).sendParticles(new DustParticleOptions(0xff610d46, 10), getX(), getY() + getBbHeight() * 0.5, getZ(), 25, getBbWidth(), getBbHeight(), getBbWidth(), 0);
                    level().playSound(null, blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1f, 0.75f);
                }
                break;
            case "idle":
                this.setAnimateState("idle");
                break;
            case "lethal_attack":
                if(phaseStateTick == 5){
                    this.setAnimateState(LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_LOOP);
                }else if(phaseStateTick == 110){
                    this.setAnimateState(LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_END);
                } else if(phaseStateTick == 160){
                    this.setAnimateState("idle");
                }
                break;
            case "circle_aoe_self":
                if(phaseStateTick == 20){
                    this.setAnimateState(CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_LOOP);
                }else if(phaseStateTick == 110){
                    this.setAnimateState(CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_END);
                }else if(phaseStateTick > 110 && phaseStateTick <= 120){
                    if(phaseStateTick % 2 == 1){
                        double dx = -Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
                        double dz = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
                        if(this.getBodyState() == Benderson.BodyState.UNFORGIVEN){
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND, SoundSource.HOSTILE, 1, 1);
                            ((ServerLevel) this.level()).sendParticles(new BlockParticleOption(ParticleTypes.DUST_PILLAR, this.level().getBlockState(this.blockPosition())), this.getX() + dx, this.getY(0.5), this.getZ() + dz, 0, dx, 0.0, dz, 0.0);
                        }else{
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), AllSoundEvents.BOSS_SWEEP_SFX.get(), SoundSource.HOSTILE, 1, 1);
                            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + dx, this.getY(0.5), this.getZ() + dz, 0, dx, 0.0, dz, 0.0);
                        }
                    }
                } else if(phaseStateTick == 160){
                    this.setAnimateState("idle");
                }
                break;
            case "circle_stack":
                if(phaseStateTick == 5){
                    this.setAnimateState(CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_LOOP);
                }else if(phaseStateTick == 100){
                    this.setAnimateState(CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_END);
                } else if(phaseStateTick == 160){
                    this.setAnimateState("idle");
                }
                break;
            case "three-fourth_arena_aoe":
                if(phaseStateTick <= 60){
                    var targetPos = this.getCombatArenaCenterVec3().subtract(0, 0, getArenaRadius() * 0.5f);
                    if(targetPos.distanceTo(position()) < 0.708 || phaseStateTick == 60){
                        phaseStateTick = 60;
                        this.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                        this.setAnimateState(PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_START);
                        var marker = new TargetMarker(this.level(), targetPos,
                                TargetMarker.MarkerArgs.complexRange(TargetMarker.MarkerType.LINEAR_AOE, this.getArenaRadius() * 2, this.getArenaRadius() * 1.5f, 130));
                        level().addFreshEntity(marker);
                    }
                } else if(phaseStateTick == 65){
                    this.setAnimateState(PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP);
                }else if(phaseStateTick == 190){
                    this.setAnimateState(PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_END);
                } else if(phaseStateTick > 190 && phaseStateTick <= 200){
                    if((phaseStateTick - 60) % 2 == 0){
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), AllSoundEvents.BOSS_SWEEP_SFX.get(), SoundSource.HOSTILE, 1, 1);
                        var zOffset = ((phaseStateTick - 60 - 130) / 2f - 1) * this.getArenaRadius() * 1.5f / 5;
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(0.5), this.getZ() + zOffset, 10, this.getArenaRadius(), 0.0, 0, 0.0);
                    }
                } else if(phaseStateTick == 240){
                    this.setAnimateState("idle");
                }
                break;
            case "summon_anticalabrum":
                if(phaseStateTick == 20){
                    var lastSword = new Anticalabrum(this.level(), this.getCombatArenaCenterVec3(), anticalabrumNextType, 600, this.getArenaRadius(), null, anticalabrumNextSeed);
                    this.level().addFreshEntity(lastSword);
                } else if(phaseStateTick == 40){
                    this.setAnimateState("idle");
                }
                break;
            case "elevate_to_extreme":
                if(phaseStateTick >= 81 && phaseStateTick <= 85){
                    if(phaseStateTick == 81){
                        this.setBodyState(Benderson.BodyState.TRANSITION_UNFORGIVEN_POST);
                        this.setAnimateState(ElevateToExtremeState.ANIMATE_STATE_P2);
                    }
                    level().playSound(null, getX(), getY() + 5, getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
                    ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.END_GATEWAY.defaultBlockState()), getX(), getY() + 5, getZ(), 64, 1, 1, 1, 0);
                } else if(phaseStateTick == 140){
                    this.setBodyState(Benderson.BodyState.UNFORGIVEN);
                    this.setAnimateState("idle");
                }
                break;
            case "knockback_from_center":
                if(phaseStateTick == 20){
                    this.setAnimateState(KnockbackFromCenterPhaseState.ANIMATE_STATE_LOOP);
                    var trackingMarker = new TargetMarker(this.level(), this.getCombatArenaCenterVec3(), TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.KNOCKBACK_RADIAL, (float) (this.getArenaRadius() * 0.75 * 2), 110));
                    this.level().addFreshEntity(trackingMarker);
                } else if(phaseStateTick == 126){
                    this.setAnimateState(KnockbackFromCenterPhaseState.ANIMATE_STATE_END);
                    Vec3 center = this.getCombatArenaCenterVec3();
                    this.teleportTo(center.x, center.y, center.z);
                } else if(phaseStateTick > 130 && phaseStateTick <= 140){
                    if(phaseStateTick == 131){
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 1, 0.5f);
                        ((ServerLevel) this.level()).sendParticles(new BlockParticleDustEmitterOption(AllParticleTypes.BLOCK_DUST_BLOWING.get(), Blocks.STONE.defaultBlockState(), (float) (this.getArenaRadius() * 0.75), 1, 5),
                                this.getX(), this.getY(), this.getZ(), 0, 0, 0.0, 0, 0.0);
                    }
                    if(phaseStateTick % 2 == 1){
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_FALL, SoundSource.HOSTILE, 1, 0.5f);
                        ((ServerLevel) this.level()).sendParticles(new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.STONE.defaultBlockState()),
                                this.getX(), this.getY(), this.getZ(), 16, 1, 0.0, 1, 0.0);
                    }
                } else if(phaseStateTick == 180){
                    this.setAnimateState("idle");
                }
                break;
            case "summon_blocking_pile":
                if(phaseStateTick == 5){
                    setAnimateState(PreEclipticMeteorState.ANIMATE_STATE_LOOP);
                } else if (phaseStateTick == 10) {
                    setAnimateState(PreEclipticMeteorState.ANIMATE_STATE_END);
                } else if(phaseStateTick == 25){
                    setAnimateState("idle");
                }
                break;
            case "ecliptic_meteor":
                Vec3 arenaCenter = this.getCombatArenaCenterVec3();
                if(phaseStateTick < 240){
                    if(phaseStateTick % 2 == 0){
                        var alpha = (float) Mth.clamp(phaseStateTick / 200.0f, 0.1, 1);
                        this.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.1f + alpha, alpha);
                    }
                    if(phaseStateTick < 170 && phaseStateTick % 5 == 0){
                        ((ServerLevel) this.level()).sendParticles(new ColoredDustEmitterOption(AllParticleTypes.DUST_SUCKING.get(), 0xffffffff, 2, (float) (this.getArenaRadius() * Math.sqrt(2)), 2, -0.49f, 5),
                                arenaCenter.x, arenaCenter.y, arenaCenter.z, 0, 0, 0.0, 0, 0.0);
                    }
                }
                if(phaseStateTick == 15){
                    setAnimateState(EclipticMeteorState.ANIMATE_STATE_LOOP);
                } else if(phaseStateTick == 160){
                    var remoteMeteor = DelayedAttackMarker.createRemoteEclipticMeteor(this.level(), arenaCenter, null, 90, 10, 0.2f);
                    this.level().addFreshEntity(remoteMeteor);
                } else if(phaseStateTick == 200){
                    this.setAnimateState(EclipticMeteorState.ANIMATE_STATE_END);
                } else if(phaseStateTick > 210 && phaseStateTick <= 240){
                    if(phaseStateTick % 5 == 0){
                        this.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 4, 0.5f);
                        ((ServerLevel) this.level()).sendParticles(new BlockParticleDustEmitterOption(AllParticleTypes.BLOCK_DUST_BLOWING.get(), Blocks.STONE.defaultBlockState(), (float) (this.getArenaRadius() * Math.sqrt(2)), 2, 5),
                                arenaCenter.x, arenaCenter.y, arenaCenter.z, 0, 0, 0.0, 0, 0.0);
                        if(phaseStateTick >= 230){
                            this.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4, 0.5f);
                        }
                    }
                } else if(phaseStateTick == 300){
                    setAnimateState("idle");
                }
                break;
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ProjectedBenderson>("Idle", test -> {
                    if(test.animatable().getBodyState().isTransition()) {
                        test.controller().reset();
                        return PlayState.STOP;
                    }
                    return test.setAndContinue(DefaultAnimations.IDLE);
                }),
                new AnimationController<ProjectedBenderson>("Special Attack", test -> {
                    if(test.animatable().getBodyState().isTransition()) {
                        test.controller().reset();
                        return PlayState.STOP;
                    }
                    var animateState = test.getDataOrDefault(BendersonDataTickets.ANIMATE_STATE, "");
                    return switch (animateState) {
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.strong_attack.start"));
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.strong_attack.loop"));
                        case LethalAttackPhaseState.ANIMATE_STATE_LETHAL_ATTACK_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.strong_attack.end"));
                        case CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.circular_swing.start"));
                        case CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.circular_swing.loop"));
                        case CircleAoeSelfPhaseState.ANIMATE_STATE_CIRCLE_AOE_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.circular_swing.end"));
                        case CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_START, PreEclipticMeteorState.ANIMATE_STATE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.magic.start"));
                        case CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_LOOP, PreEclipticMeteorState.ANIMATE_STATE_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.magic.loop"));
                        case CircleStackAttackPhaseState.ANIMATE_STATE_CIRCLE_STACK_ATTACK_END, PreEclipticMeteorState.ANIMATE_STATE_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.magic.end"));
                        case PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.facing_swing.start"));
                        case PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.facing_swing.loop"));
                        case PartialArenaAoePhaseState.ANIMATE_STATE_HALF_ARENA_AOE_SELF_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("attack.facing_swing.end"));
                        case SummonAnticalabrumPhaseState.ANIMATE_STATE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("cast.sword"));
                        case KnockbackFromCenterPhaseState.ANIMATE_STATE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("knockback_at_center.start"));
                        case KnockbackFromCenterPhaseState.ANIMATE_STATE_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("knockback_at_center.loop"));
                        case KnockbackFromCenterPhaseState.ANIMATE_STATE_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("knockback_at_center.end"));
                        case EclipticMeteorState.ANIMATE_STATE_START ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("ecliptic_meteor.start"));
                        case EclipticMeteorState.ANIMATE_STATE_LOOP ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("ecliptic_meteor.loop"));
                        case EclipticMeteorState.ANIMATE_STATE_END ->
                                test.setAndContinue(RawAnimation.begin().thenPlay("ecliptic_meteor.end"));
                        case "idle" -> {
                            test.controller().reset();
                            yield PlayState.STOP;
                        }
                        default -> PlayState.STOP;
                    };
                }),
                new AnimationController<ProjectedBenderson>("Special Performing", test -> {
                    var animateState = test.getDataOrDefault(BendersonDataTickets.ANIMATE_STATE, "");
                    if(test.animatable().getBodyState() == Benderson.BodyState.ENTRANCE
                            && Objects.equals(animateState, ArenaEnteringPhaseState.ANIMATE_STATE_START)){
                        return test.setAndContinue(RawAnimation.begin().thenPlay("sweep_arena"));
                    }else if(test.animatable().getBodyState() == Benderson.BodyState.TRANSITION_UNFORGIVEN){
                        return test.setAndContinue(RawAnimation.begin().thenPlay("change_to_unforgiven"));
                    }else if(test.animatable().getBodyState() == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST){
                        return test.setAndContinue(RawAnimation.begin().thenPlay("change_to_unforgiven_p2"));
                    }
                    test.controller().reset();
                    return PlayState.STOP;
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public void setBodyState(Benderson.BodyState bodyState){
        entityData.set(BODY_STATE, bodyState);
    }

    public Benderson.BodyState getBodyState() {
        return entityData.get(BODY_STATE);
    }

    @Override
    public boolean isShouldHideBoundingBox() {
        return entityData.get(SHOULD_HIDE_BOUNDING_BOX);
    }

    private void setAnimateState(String animateState){
        entityData.set(ANIMATE_STATE, animateState);
    }

    @Override
    public String getAnimateState() {
        return entityData.get(ANIMATE_STATE);
    }

    public void setArenaRadius(int radius){
        entityData.set(ARENA_RADIUS, radius);
    }

    @Override
    public int getArenaRadius() {
        return entityData.get(ARENA_RADIUS);
    }

    @Override
    public Vec3 getCombatArenaCenterVec3() {
        return Vec3.atLowerCornerOf(getArenaCenter());
    }

    public void setArenaCenter(BlockPos pos){
        this.entityData.set(ARENA_CENTER, Optional.of(pos));
    }

    public BlockPos getArenaCenter(){
        return entityData.get(ARENA_CENTER).orElse(BlockPos.ZERO);
    }

    @Override
    public Component getName() {
        if(this.hasCustomName()) return super.getName();
        MutableComponent baseComp;
        if(this.getBodyState() == Benderson.BodyState.UNFORGIVEN || this.getBodyState() == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST){
            baseComp = Component.translatable("entity.lanfasie_benderson.benderson.name.unforgiven");
        }
        else baseComp = Component.translatable("entity.lanfasie_benderson.benderson.name.deep_latent");
        return baseComp.append(Component.translatable("misc.lanfasie_benderson.projected_suffix"));
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, (double)1000.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.CAMERA_DISTANCE, (double)16.0F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0f)
                .add(AllAttributes.EXTREME, 0);
    }
}
