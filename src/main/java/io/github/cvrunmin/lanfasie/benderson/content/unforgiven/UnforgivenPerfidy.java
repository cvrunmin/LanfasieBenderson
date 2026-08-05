package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class UnforgivenPerfidy extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_SWELLED = SynchedEntityData.defineId(UnforgivenPerfidy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(UnforgivenPerfidy.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_SWELLING = SynchedEntityData.defineId(UnforgivenPerfidy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions SWELLED_DIMENSIONS = EntityDimensions.scalable(0.5f, 0.5f).withEyeHeight(0.35f);

    private static final short DEFAULT_MAX_SWELL = 60;
    private static final short DEFAULT_EXPLOSION_RADIUS = 3;
    private int oldSwell;
    private int swell;
    private int maxSwell = 60;
    private int explosionRadius = 3;

    private AvoidEntityGoal<LivingEntity> swelledAvoidingEntityGoal = new AvoidEntityGoal<>(this, LivingEntity.class, entity -> !(entity instanceof UnforgivenPerfidy), 6.0f, 1.0, 1.2, EntitySelector.NO_CREATIVE_OR_SPECTATOR);

    public UnforgivenPerfidy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public UnforgivenPerfidy(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_PERFIDY.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.FOLLOW_RANGE, 8).build();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SWELLED, false);
        entityData.define(DATA_SWELL_DIR, -1);
        entityData.define(DATA_IS_SWELLING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PerfidySwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, EnderMan.class, 8.0F, 1.0, 1.2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new PerfidyLimitedRangeNearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public void setSwelled(boolean swelled) {
        entityData.set(DATA_SWELLED, swelled);
        if(swelled){
            this.goalSelector.addGoal(3, swelledAvoidingEntityGoal);
        }else{
            this.goalSelector.removeGoal(swelledAvoidingEntityGoal);
        }
    }

    public @NotNull Boolean isSwelled() {
        return entityData.get(DATA_SWELLED);
    }

    public void setSwellDir(int dir){
        entityData.set(DATA_SWELL_DIR, dir);
    }

    public int getSwellDir(){
        return entityData.get(DATA_SWELL_DIR);
    }

    public void setIsSwelling(boolean flag){
        entityData.set(DATA_IS_SWELLING, flag);
    }

    public boolean isSwelling(){
        return entityData.get(DATA_IS_SWELLING);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if(accessor == DATA_SWELLED) {
            this.oldSwell = 0;
            this.swell = 0;
            refreshDimensions();
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return isSwelled() ? SWELLED_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setSwelled(input.getBooleanOr("Swelled", false));
        setIsSwelling(input.getBooleanOr("powered", false));
        this.maxSwell = input.getShortOr("Fuse", DEFAULT_MAX_SWELL);
        this.explosionRadius = input.getShortOr("ExplosionRadius", DEFAULT_EXPLOSION_RADIUS);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Swelled", isSwelled());
        output.putBoolean("powered", isSwelling());
        output.putShort("Fuse", (short)this.maxSwell);
        output.putShort("ExplosionRadius", (short)this.explosionRadius);
    }

    public float getSwelling(float a) {
        return Mth.lerp(a, (float)this.oldSwell, (float)this.swell) / (this.maxSwell - 2);
    }

    @Override
    public void tick() {
        if(this.isAlive()){
            this.oldSwell = swell;
            int swellDir = getSwellDir();
            if(this.isSwelled() && swellDir > 0) {
                setSwellDir(-1);
                swellDir = -1;
            }
            if (swellDir > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
            this.swell += swellDir;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if(this.oldSwell < this.maxSwell - 30 && this.swell >= this.maxSwell - 30){
                var target = getTarget();
                if(target != null){
                    teleportToBack(target);
                }
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explode();
            }
        }
        super.tick();
    }

    public boolean inLateSwellStage(){
        return swell >= this.maxSwell - 30;
    }

    private void explode(){
        if(this.level() instanceof ServerLevel level){
            level.explode(this, this.getX(), this.getY(), this.getZ(), this.explosionRadius, Level.ExplosionInteraction.TRIGGER);
            this.swell = 0;
            this.oldSwell = 0;
            this.setSwelled(true);
        }
    }

    private boolean isBeingStared(LivingEntity target){
        return this.isLookingAtMe(target, 0.292893, true, false, this.getEyeY(), this.getY() + 0.5 * this.getScale(), (this.getEyeY() + this.getY()) / 2.0);
    }

    private boolean isPosOccupied(Vec3 vec3){
        if (this.noPhysics) {
            return false;
        } else {
            AABB bb = this.makeBoundingBox(vec3);
            return BlockPos.betweenClosedStream(bb)
                    .anyMatch(
                            pos -> {
                                BlockState state = this.level().getBlockState(pos);
                                return !state.isAir()
                                        && state.isSuffocating(this.level(), pos)
                                        && Shapes.joinIsNotEmpty(state.getCollisionShape(this.level(), pos).move(pos), Shapes.create(bb), BooleanOp.AND);
                            }
                    );
        }
    }

    private boolean teleportToBack(LivingEntity target){
        var viewVec = target.getViewVector(1);
        var backVec = viewVec.horizontal().normalize().reverse();
        var currentDist = 2.0;
        Vec3 tgtPos;
        do {
            tgtPos = target.position().add(backVec.scale(currentDist));
            currentDist -= 0.5;
        } while (isPosOccupied(tgtPos) && currentDist > 0);

        return teleport(tgtPos.x, tgtPos.y, tgtPos.z);
    }

    private boolean teleport(double x, double y, double z){
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

        while (pos.getY() > this.level().getMinY() && !this.level().getBlockState(pos).blocksMotion()) {
            pos.move(Direction.DOWN);
        }

        BlockState blockState = this.level().getBlockState(pos);
        boolean couldStandOn = blockState.blocksMotion();
        if (couldStandOn) {
            Vec3 oldPos = this.position();
            boolean result = this.randomTeleport(x, y, z, true);
            if (result) {
                this.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return result;
        } else {
            return false;
        }
    }

    public static class PerfidyLimitedRangeNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>{

        protected final boolean preferSee;

        public PerfidyLimitedRangeNearestAttackableTargetGoal(Mob mob, Class<T> targetType, boolean mustSee, boolean preferSee) {
            super(mob, targetType, mustSee);
            this.preferSee = preferSee;
        }

        @Override
        protected void findTarget() {
            ServerLevel level = getServerLevel(this.mob);
            if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
                this.target = level.getNearestEntity(
                        this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), entity -> !this.preferSee || this.mob.getSensing().hasLineOfSight(entity)),
                        this.getTargetConditions(),
                        this.mob,
                        this.mob.getX(),
                        this.mob.getEyeY(),
                        this.mob.getZ()
                );
            } else {
                var targetConditions = this.getTargetConditions();
                if(!preferSee){
                    targetConditions = targetConditions.ignoreLineOfSight();
                }
                this.target = level.getNearestPlayer(targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }
        }

        private TargetingConditions getTargetConditions() {
            return this.targetConditions.copy().range(8.0);
        }
    }

    public static class PerfidySwellGoal extends Goal {

        private final UnforgivenPerfidy mob;
        private @Nullable LivingEntity target;

        public PerfidySwellGoal(UnforgivenPerfidy mob){
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(mob.isSwelled()) return false;
            LivingEntity target = this.mob.getTarget();
            if(mob.getSwellDir() > 0) return true;
            if(target == null || target.isDeadOrDying()) return false;
            return mob.swell == 0 && mob.distanceToSqr(target) < 64;
        }

        @Override
        public void start() {
            this.mob.getNavigation().stop();
            this.target = this.mob.getTarget();
        }

        @Override
        public void stop() {
            this.target = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if(this.target != null && !this.target.isDeadOrDying()){
                if(this.mob.inLateSwellStage()){
                    if (this.mob.distanceToSqr(this.target) > 25.0) {
                        this.mob.setSwellDir(-1);
                    } else if (!this.mob.getSensing().hasLineOfSight(this.target)) {
                        this.mob.setSwellDir(-1);
                    } else if(this.mob.isBeingStared(this.target)) {
                        this.mob.setSwellDir(-1);
                    } else {
                        this.mob.setSwellDir(1);
                    }
                }else if(this.mob.swell == 0){
                    this.mob.setSwellDir(1);
                }
            }else{
                this.mob.setSwellDir(-1);
            }
        }
    }
}
