package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class UnforgivenRidicule extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(UnforgivenRidicule.class, EntityDataSerializers.BOOLEAN);

    public UnforgivenRidicule(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public UnforgivenRidicule(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_RIDICULE.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.ATTACK_DAMAGE, 1.5).add(Attributes.MOVEMENT_SPEED, 0.25).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new SonicAttackGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, UnforgivenIndiscretion.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EnderMan.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_IS_CHARGING, false);
    }

    public void setCharged(boolean flag){
        this.entityData.set(DATA_IS_CHARGING, flag);
    }

    public boolean isCharged(){
        return this.entityData.get(DATA_IS_CHARGING);
    }

    private static class SonicAttackGoal extends Goal{
        private UnforgivenRidicule mob;
        private int attackTime;
        private int lastSeen;

        private SonicAttackGoal(UnforgivenRidicule mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive() && this.mob.canAttack(target);
        }

        @Override
        public void start() {
            attackTime = 100;
            this.mob.setCharged(true);
        }

        @Override
        public void stop() {
            this.mob.setCharged(false);
            lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            this.attackTime--;
            var target = mob.getTarget();
            if(target != null){
                var hasLineOfSight = mob.getSensing().hasLineOfSight(target);
                if(hasLineOfSight){
                    lastSeen = 0;
                }
                else{
                    lastSeen++;
                }
                double dist = this.mob.distanceToSqr(target);
                double followDistance = getFollowDistance();
                if(dist < followDistance * followDistance && hasLineOfSight){
                    if(attackTime <= 0){
                        attackTime = 60;
                        if(!mob.level().isClientSide()){
                            var source = mob.position().add(0, mob.getY(0.8) - mob.getY(), 0);
                            var delta = target.getEyePosition().subtract(source);
                            var normalize = delta.normalize();
                            int steps = Mth.floor(delta.length()) + 7;
                            for (int i = 1; i < steps; i++) {
                                Vec3 particlePos = source.add(normalize.scale(i));
                                ((ServerLevel) mob.level()).sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                            }
                            mob.gameEvent(GameEvent.RESONATE_15);
                            mob.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                            if (target.hurtServer(((ServerLevel) mob.level()), mob.damageSources().sonicBoom(mob), 10.0F)) {
                                double knockbackVertical = 0.125 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                double knockbackHorizontal = 0.625 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                target.push(normalize.x() * knockbackHorizontal, normalize.y() * knockbackVertical, normalize.z() * knockbackHorizontal);
                            }
                        }
                    }
                    this.mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.mob.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
                }
            }
        }

        private double getFollowDistance() {
            return this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}
