package io.github.cvrunmin.lanfasie.benderson.content.effects;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class AggroUpMobEffect extends MobEffect {
    public AggroUpMobEffect(MobEffectCategory category, int color) {
        super(category, color, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }

    @SubscribeEvent
    public static void angerIfAggroUp(LivingDamageEvent.Post event){
        if(event.getEntity() instanceof Mob mob
                && (event.getEntity() instanceof Enemy || event.getEntity() instanceof NeutralMob)
                && event.getSource().getDirectEntity() != null
                && event.getSource().getDirectEntity() instanceof LivingEntity livingEntity
                && livingEntity.isAlive()
            && livingEntity.hasEffect(AllMobEffects.AGGRO_UP)){
            mob.setTarget(livingEntity);
        }
    }
}
