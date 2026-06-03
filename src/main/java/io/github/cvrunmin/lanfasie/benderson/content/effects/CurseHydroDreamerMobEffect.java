package io.github.cvrunmin.lanfasie.benderson.content.effects;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class CurseHydroDreamerMobEffect extends MobEffect {
    public CurseHydroDreamerMobEffect(MobEffectCategory category, int color) {
        super(category, color, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event){
        if(event.getEntity() instanceof Player player) {
            if (event.getEffectInstance().is(AllMobEffects.CURSE_HYDRO_DREAMER)) {
                ((ISaturationBreaker) player.getFoodData()).setSaturationLimitBreak(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event){
        if(event.getEntity() instanceof Player player){
            if (event.getEffect().is(AllMobEffects.CURSE_HYDRO_DREAMER)) {
                ((ISaturationBreaker) player.getFoodData()).setSaturationLimitBreak(false);
            }
        }
    }
}
