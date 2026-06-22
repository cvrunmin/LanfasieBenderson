package io.github.cvrunmin.lanfasie.benderson.content.effects;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SummerSeptetMobEffect extends MobEffect {
    public SummerSeptetMobEffect(MobEffectCategory category) {
        super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }
}
