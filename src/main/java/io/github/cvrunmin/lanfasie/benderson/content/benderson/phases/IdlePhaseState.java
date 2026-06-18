package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class IdlePhaseState implements IPhaseState{

    private final Benderson owner;
    private int currentTick;

    public IdlePhaseState(Benderson owner){
        this.owner = owner;
    }

    @Override
    public void start() {
        this.currentTick = 0;
        var center = this.owner.getCombatArenaCenterVec3();
        this.owner.teleportTo(center.x, center.y, center.z);
        this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, 1).add(this.owner.position()));
    }

    @Override
    public boolean tick() {
        this.currentTick++;
        return this.currentTick <= 20;
    }

    @Override
    public void end() {
        this.currentTick = 0;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
    }
}
