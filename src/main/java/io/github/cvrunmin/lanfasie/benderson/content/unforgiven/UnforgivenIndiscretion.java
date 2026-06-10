package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class UnforgivenIndiscretion extends Monster {
    public UnforgivenIndiscretion(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public UnforgivenIndiscretion(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.25).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnnounceTargetGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    private static class AnnounceTargetGoal extends Goal{
        private final UnforgivenIndiscretion guy;
        private int tick;

        private AnnounceTargetGoal(UnforgivenIndiscretion guy) {
            this.guy = guy;
        }

        @Override
        public boolean canUse() {
            return this.guy.getTarget() != null && this.guy.getTarget().canBeSeenAsEnemy();
        }

        @Override
        public void start() {
            tick = 0;
        }

        @Override
        public void tick() {
            if(tick++ % 10 == 0){
                LivingEntity target = this.guy.getTarget();
                double within = this.guy.getAttributeValue(Attributes.FOLLOW_RANGE);
                AABB searchAabb = AABB.unitCubeFromLowerCorner(this.guy.position()).inflate(within, 10.0, within);

                var nearby = this.guy.level().getEntitiesOfClass(Mob.class, searchAabb, EntitySelector.NO_SPECTATORS);
                for (Mob entity : nearby) {
                    if(entity.getTarget() == null && (entity instanceof Enemy || entity instanceof NeutralMob)){
                        entity.setTarget(target);
                    }
                }
            }
        }
    }
}
