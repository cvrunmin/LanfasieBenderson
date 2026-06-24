package io.github.cvrunmin.lanfasie.benderson.content.effects;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllDataAttachments;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Arrays;
import java.util.Optional;

public class MundaneTrioMobEffect extends MobEffect {
    public MundaneTrioMobEffect(MobEffectCategory category) {
        super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        var bardSongs = new Boolean[3];
        bardSongs[0] = mob.hasEffect(AllMobEffects.OPENING_MINUET);
        bardSongs[1] = mob.hasEffect(AllMobEffects.TWIN_BALLAD);
        bardSongs[2] = mob.hasEffect(AllMobEffects.BELOVED_PAEAN);
        if (Arrays.stream(bardSongs).noneMatch(f -> f)) {
            mob.addEffect(new MobEffectInstance(AllMobEffects.OPENING_MINUET, 900, 0, false, false, true));
        }else if(bardSongs[2]){
            if(Optional.ofNullable(mob.getEffect(AllMobEffects.BELOVED_PAEAN)).map(i -> i.endsWithin(240)).orElse(false)){
                mob.removeEffect(AllMobEffects.BELOVED_PAEAN);
                mob.addEffect(new MobEffectInstance(AllMobEffects.OPENING_MINUET, 900, 0, false, false, true));
            }
        } else if(bardSongs[1]){
            if(Optional.ofNullable(mob.getEffect(AllMobEffects.TWIN_BALLAD)).map(i -> i.endsWithin(60)).orElse(false)){
                mob.removeEffect(AllMobEffects.TWIN_BALLAD);
                mob.addEffect(new MobEffectInstance(AllMobEffects.BELOVED_PAEAN, 900, 0, false, false, true));
            }
        } else if(bardSongs[0]){
            if(Optional.ofNullable(mob.getEffect(AllMobEffects.OPENING_MINUET)).map(i -> i.endsWithin(60)).orElse(false)){
                mob.removeEffect(AllMobEffects.OPENING_MINUET);
                mob.addEffect(new MobEffectInstance(AllMobEffects.TWIN_BALLAD, 900, 0, false, false, true));
            }
        }
        return true;
    }

    public static class MundaneTrioBigOrangeMobEffect extends MobEffect{

        public MundaneTrioBigOrangeMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
            return tickCount % 60 == 0;
        }
    }
    public static class MundaneTrioLittleOrangeMobEffect extends MobEffect{

        public MundaneTrioLittleOrangeMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
            return tickCount % 60 == 0;
        }
    }
    public static class MundaneTrioPentaMobEffect extends MobEffect{

        public MundaneTrioPentaMobEffect(MobEffectCategory category) {
            super(category, 0, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
            return tickCount % 60 == 0;
        }

        @Override
        public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
            int heartLevel = mob.getData(AllDataAttachments.POETIC_SOUL);
            var oldHeart = heartLevel;
            if(heartLevel < 4 && mob.getRandom().nextFloat() < 0.8){
                heartLevel++;
            }
            AttributeInstance attribute = mob.getAttribute(Attributes.ATTACK_SPEED);
            if(attribute != null) {
                attribute.addOrUpdateTransientModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "poetic_soul"), 0.04 * heartLevel, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            if(oldHeart != heartLevel) {
                mob.setData(AllDataAttachments.POETIC_SOUL, heartLevel);
            }
            return true;
        }
    }
}
