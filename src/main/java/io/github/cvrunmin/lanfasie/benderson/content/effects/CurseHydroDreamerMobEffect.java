package io.github.cvrunmin.lanfasie.benderson.content.effects;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CurseHydroDreamerMobEffect extends MobEffect {
    public CurseHydroDreamerMobEffect(MobEffectCategory category, int color) {
        super(category, color, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }
}
