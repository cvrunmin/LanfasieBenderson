package io.github.cvrunmin.lanfasie.benderson.content.effects;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CurseNetherDogMobEffect extends MobEffect {
    public CurseNetherDogMobEffect(MobEffectCategory category, int color) {
        super(category, color, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }
}
