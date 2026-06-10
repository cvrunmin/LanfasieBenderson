package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class UnforgivenSpoiling extends Monster {
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;
    public final AnimationState hopAnimationState = new AnimationState();

    public UnforgivenSpoiling(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.jumpControl = new HareJumpControl(this);
        this.moveControl = new HareMoveControl(this);
        this.setSpeedModifier(0.0);
    }

    public UnforgivenSpoiling(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_SPOILING.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.2).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    protected float getJumpPower() {
        float baseJumpPower = 0.3F;
        if (this.moveControl.getSpeedModifier() <= 0.6) {
            baseJumpPower = 0.2F;
        }

        Path path = this.navigation.getPath();
        if (path != null && !path.isDone()) {
            Vec3 currentPos = path.getNextEntityPos(this);
            if (currentPos.y > this.getY() + 0.5) {
                baseJumpPower = 0.5F;
            }
        }

        if (this.horizontalCollision || this.jumping && this.moveControl.getWantedY() > this.getY() + 0.5) {
            baseJumpPower = 0.5F;
        }

        return super.getJumpPower(baseJumpPower / 0.42F);
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        double speedModifier = this.moveControl.getSpeedModifier();
        if (speedModifier > 0.0) {
            double current = this.getDeltaMovement().horizontalDistanceSqr();
            if (current < 0.01) {
                this.moveRelative(0.1F, new Vec3(0.0, this.isBaby() ? 0.5 : 1.5, 1.0));
            }
        }

        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)1);
        }
    }

    public float getJumpCompletion(float partialTicks) {
        return this.jumpDuration == 0 ? 0.0F : (this.jumpTicks + partialTicks) / this.jumpDuration;
    }

    public void setSpeedModifier(double speed) {
        this.getNavigation().setSpeedModifier(speed);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), speed);
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 15;
        this.jumpTicks = 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.jumpTicks != this.jumpDuration) {
            this.jumpTicks++;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (this.jumpDelayTicks > 0) {
            this.jumpDelayTicks--;
        }
        if (this.onGround()) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            var jumpControl = ((HareJumpControl) this.jumpControl);
            if (!jumpControl.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    Path path = this.navigation.getPath();
                    Vec3 pos = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && !path.isDone()) {
                        pos = path.getNextEntityPos(this);
                    }

                    this.facePoint(pos.x, pos.z);
                    this.startJumping();
                }
            } else if (!jumpControl.canJump()) {
                this.enableJumpControl();
            }
        }
        this.wasOnGround = this.onGround();
    }

    private void facePoint(double faceX, double faceZ) {
        this.setYRot((float)(Mth.atan2(faceZ - this.getZ(), faceX - this.getX()) * 180.0F / (float)Math.PI) - 90.0F);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 1) {
            this.spawnSprintParticle();
            this.jumpDuration = 15;
            this.jumpTicks = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }


    private void enableJumpControl() {
        ((HareJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((HareJumpControl)this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        this.jumpDelayTicks = this.moveControl.getSpeedModifier() < 2.2 ? 10 : 3;
    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    private void setupAnimationStates() {
        if (this.jumpTicks > 0) {
            this.hopAnimationState.startIfStopped(this.tickCount);
        } else {
            this.hopAnimationState.stop();
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private static class HareJumpControl extends JumpControl{
        private final UnforgivenSpoiling hare;
        private boolean canJump;

        public HareJumpControl(UnforgivenSpoiling hare) {
            super(hare);
            this.hare = hare;
        }

        public boolean wantJump(){
            return this.jump;
        }

        public boolean canJump(){
            return this.canJump;
        }

        public void setCanJump(boolean canJump) {
            this.canJump = canJump;
        }

        @Override
        public void tick() {
            if(this.jump){
                this.hare.startJumping();
                this.jump = false;
            }
        }
    }

    private static class HareMoveControl extends MoveControl{
        private final UnforgivenSpoiling hare;
        private double nextJumpSpeed;

        public HareMoveControl(UnforgivenSpoiling mob) {
            super(mob);
            hare = mob;
        }

        @Override
        public void tick() {
            if(this.hare.onGround() && !this.hare.jumping && !((HareJumpControl) this.hare.jumpControl).wantJump()){
                this.hare.setSpeedModifier(0.0);
            } else if(this.hasWanted() || this.operation == Operation.JUMPING){
                this.hare.setSpeedModifier(nextJumpSpeed);
            }
            super.tick();
        }

        @Override
        public void setWantedPosition(double x, double y, double z, double speedModifier) {
            if(this.hare.isInWater()){
                speedModifier = 1.5;
            }
            super.setWantedPosition(x, y, z, speedModifier);
            if(speedModifier > 0){
                this.nextJumpSpeed = speedModifier;
            }
        }
    }
}
