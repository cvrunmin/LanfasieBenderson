package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class SummonAnticalabrumPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "summon_anticalabrum.start";
    private final Benderson owner;
    private Anticalabrum.AnticalabrumType nextType = Anticalabrum.AnticalabrumType.FELIS_INVISIBILIS;
    private int currentTick = 0;
    private Anticalabrum lastSword;
    private int cooldownTick = 0;

    public SummonAnticalabrumPhaseState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        this.owner.setAnimateState(ANIMATE_STATE_START);
        currentTick = 0;
    }

    @Override
    public boolean tick() {
        currentTick++;
        if(currentTick == 20){
            if(!this.owner.level().isClientSide()){
                lastSword = new Anticalabrum(this.owner.level(), this.owner.getCombatArenaCenterVec3(), nextType, 600, this.owner.getArenaRadius(), this.owner);
                this.owner.level().addFreshEntity(lastSword);
                nextType = Anticalabrum.AnticalabrumType.values()[nextType.getNextTypeIndex()];
            }
        } else if (currentTick >= 40) {
            return false;
        }
        return true;
    }

    @Override
    public void end() {
        this.owner.setAnimateState("idle");
        this.currentTick = 0;
        this.cooldownTick = 800;
    }

    @Override
    public void inactiveTick() {
        if(this.cooldownTick > 0) this.cooldownTick--;
    }

    @Override
    public boolean canUse() {
        return cooldownTick <= 0 && this.owner.getBodyState() == Benderson.BodyState.DEEP_LATENT && this.owner.getTarget() != null && (lastSword == null || lastSword.isRemoved());
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Cooldown", this.cooldownTick);
        output.putInt("Tick", this.currentTick);
        output.store("NextType", Anticalabrum.AnticalabrumType.CODEC, this.nextType);
        if(lastSword != null) {
            EntityReference.of(lastSword).store(output, "LastSword");
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.cooldownTick = input.getIntOr("Cooldown", 0);
        this.currentTick = input.getIntOr("Tick", this.currentTick);
        input.read("NextType", Anticalabrum.AnticalabrumType.CODEC).ifPresent(v -> this.nextType = v);
        Optional.ofNullable(EntityReference.<Anticalabrum>read(input, "LastSword"))
                .map(ref -> ref.getEntity(this.owner.level(), Anticalabrum.class))
                .ifPresent(v -> lastSword = v);
    }
}
