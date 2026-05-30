package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class NormalAttackPhaseState implements IPhaseState{
    private final Benderson owner;
    private LivingEntity currentTarget;
    private int currentTick = 0;

    public NormalAttackPhaseState(Benderson owner){
        this.owner = owner;
    }
    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        if(this.owner.getTarget() != null){
            currentTarget = this.owner.getTarget();
            var distVec = this.owner.position().subtract(currentTarget.position());
            if(distVec.length() > 3.0f){
                var newPos = currentTarget.position().add(distVec.horizontal().normalize());
                this.owner.teleportTo(newPos.x, newPos.y, newPos.z);
            }
            this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, currentTarget.position());
            this.owner.swing(InteractionHand.MAIN_HAND);
            this.currentTick = 0;
        }
    }

    @Override
    public boolean tick() {
        currentTick++;
        if(currentTick == 7){
            if(this.currentTarget != null) {
                this.owner.doHurtTarget(((ServerLevel) this.owner.level()), this.currentTarget);
            }
        } else if (currentTick >= 30) {
            currentTarget = null;
            return false;
        }
        return true;
    }

    @Override
    public void end() {

    }

    @Override
    public boolean canUse() {
        return this.owner.getTarget() != null;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
        this.currentTarget = this.owner.getTarget();
    }
}
