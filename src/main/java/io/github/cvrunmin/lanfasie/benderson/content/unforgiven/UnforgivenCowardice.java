package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;

public class UnforgivenCowardice extends Monster {
    public UnforgivenCowardice(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public UnforgivenCowardice(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_COWARDICE.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.175f).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(12, new FollowNearbyHareGoal(this, 1.25f));
        this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(0, new CowardiceHurtByTargetGoal(this).setAlertOthers());
    }

    private static class FollowNearbyHareGoal extends Goal {
        private final UnforgivenCowardice owner;
        private final double speedModifier;
        private UnforgivenSpoiling hare;
        private int timeToRecalcPath;

        public FollowNearbyHareGoal(UnforgivenCowardice owner, double speedModifier) {
            this.owner = owner;
            this.speedModifier = speedModifier;
        }

        @Override
        public boolean canUse() {
            var closestHare = this.owner.level()
                    .getEntitiesOfClass(UnforgivenSpoiling.class, this.owner.getBoundingBox().inflate(8, 4, 8))
                    .stream().min(Comparator.comparing(this.owner::distanceToSqr)).orElse(null);
            if(closestHare == null) return false;
            var dist = this.owner.distanceToSqr(closestHare);
            if(dist  < 9.0) return false;
            this.hare = closestHare;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if(hare == null || !hare.isAlive()) return false;
            var dist = this.owner.distanceToSqr(hare);
            return 9 <= dist && dist <= 1024;
        }

        @Override
        public void start() {
            timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            hare = null;
        }

        @Override
        public void tick() {
            if(--this.timeToRecalcPath <= 0){
                this.timeToRecalcPath = adjustedTickDelay(10);
                this.owner.getNavigation().moveTo(this.hare, speedModifier);
            }
        }
    }

    private static class CowardiceHurtByTargetGoal extends HurtByTargetGoal{
        private final UnforgivenCowardice cowardice;
        public CowardiceHurtByTargetGoal(UnforgivenCowardice mob, Class<?>... ignoreDamageFromTheseTypes) {
            super(mob, ignoreDamageFromTheseTypes);
            cowardice = mob;
        }

        @Override
        protected void alertOthers() {
            double within = this.getFollowDistance();
            AABB searchAabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(within, 10.0, within);

            var nearby = this.mob.level().getEntitiesOfClass(UnforgivenCowardice.class, searchAabb, EntitySelector.NO_SPECTATORS);
            for (UnforgivenCowardice other : nearby) {
                if(this.mob != other && other.getTarget() == null && !other.isAlliedTo(this.mob.getLastHurtByMob())){
                    this.alertOther(other, this.mob.getLastHurtByMob());
                }
            }
            var nearby1 = this.mob.level().getEntitiesOfClass(UnforgivenSpoiling.class, searchAabb, EntitySelector.NO_SPECTATORS);
            for (UnforgivenSpoiling other : nearby1) {
                if(other.getTarget() == null && !other.isAlliedTo(this.mob.getLastHurtByMob())){
                    this.alertOther(other, this.mob.getLastHurtByMob());
                }
            }
        }
    }
}
