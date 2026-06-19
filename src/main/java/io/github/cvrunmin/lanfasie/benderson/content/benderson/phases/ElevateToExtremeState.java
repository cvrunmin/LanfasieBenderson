package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.OptionalDouble;

public class ElevateToExtremeState implements IPhaseState{
    public static final String ANIMATE_STATE_P1 = "elevate_to_extreme_p1";
    public static final String ANIMATE_STATE_P2 = "elevate_to_extreme_p2";
    private final Benderson owner;
    private int tick;

    public ElevateToExtremeState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        tick = 0;
        owner.setAnimateState(ANIMATE_STATE_P1);
        owner.setBodyState(Benderson.BodyState.TRANSITION_UNFORGIVEN);
    }

    @Override
    public boolean tick() {
        if(tick >= 140) return false;
        this.owner.suppressBossBarUpdate = true;
        tick++;
        this.owner.getBossEvent().setProgress(tick / 140.0f);
        if(tick == 81){
            owner.setAnimateState(ANIMATE_STATE_P2);
            owner.setBodyState(Benderson.BodyState.TRANSITION_UNFORGIVEN_POST);
            owner.level().playSound(null, owner.getX(), owner.getY() + 5, owner.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
            ((ServerLevel) owner.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.END_GATEWAY.defaultBlockState()), owner.getX(), owner.getY() + 5, owner.getZ(), 64, 1, 1, 1, 0);
        }else if(tick > 81 && tick <=85){
            ((ServerLevel) owner.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.END_GATEWAY.defaultBlockState()), owner.getX(), owner.getY() + 5, owner.getZ(), 64, 1, 1, 1, 0);
            owner.level().playSound(null, owner.getX(), owner.getY() + 5, owner.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
        }
        return true;
    }

    @Override
    public void end() {
        owner.setHealth(owner.getMaxHealth());
        owner.setBodyState(Benderson.BodyState.UNFORGIVEN);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", tick);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        input.getInt("Tick").ifPresent(v -> tick = v);
    }

    @Override
    public boolean canUse() {
        return owner.getBodyState() == Benderson.BodyState.DEEP_LATENT;
    }

    @Override
    public OptionalDouble syncSecondForClient() {
        var offsetTick = tick;
        if(offsetTick >= 81){
            offsetTick -= 81;
        }
        return OptionalDouble.of(offsetTick / 20.0);
    }
}
