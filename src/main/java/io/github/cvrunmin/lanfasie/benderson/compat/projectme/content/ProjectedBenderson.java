package io.github.cvrunmin.lanfasie.benderson.compat.projectme.content;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonDataTickets;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonStatesGetter;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.*;
import io.github.cvrunmin.lanfasie.benderson.index.AllAttributes;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityDataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
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


    public ProjectedBenderson(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
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

    public Benderson.BodyState getBodyState() {
        return entityData.get(BODY_STATE);
    }

    @Override
    public boolean isShouldHideBoundingBox() {
        return entityData.get(SHOULD_HIDE_BOUNDING_BOX);
    }

    @Override
    public String getAnimateState() {
        return entityData.get(ANIMATE_STATE);
    }

    @Override
    public int getArenaRadius() {
        return entityData.get(ARENA_RADIUS);
    }

    @Override
    public Vec3 getCombatArenaCenterVec3() {
        return Vec3.atLowerCornerOf(getArenaCenter());
    }

    public BlockPos getArenaCenter(){
        return entityData.get(ARENA_CENTER).orElse(BlockPos.ZERO);
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
