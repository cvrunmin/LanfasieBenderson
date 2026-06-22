package io.github.cvrunmin.lanfasie.benderson.content.effects;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MundaneTrioMobEffect extends MobEffect {
    public MundaneTrioMobEffect(MobEffectCategory category) {
        super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }

    public static class MundaneTrioBigOrangeMobEffect extends MobEffect{

        public MundaneTrioBigOrangeMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }
    }
    public static class MundaneTrioLittleOrangeMobEffect extends MobEffect{

        public MundaneTrioLittleOrangeMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }
    }
    public static class MundaneTrioPentaMobEffect extends MobEffect{

        public MundaneTrioPentaMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }
    }
}
