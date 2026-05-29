package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class MobEffectRemovalProtector {
    private static ConcurrentHashMap<LivingEntity, Holder<MobEffect>> granted = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event){
        if(event.getEntity().level().isClientSide()) return; // do not block if in client level
        if(event.getEffect().is(AllMobEffects.AGGRO_UP)){
            if(!granted.containsKey(event.getEntity()) || !granted.get(event.getEntity()).equals(AllMobEffects.AGGRO_UP)) {
                event.setCanceled(true);
            }
        }
        else if(event.getEffect().is(AllMobEffects.VULNERABILITY_UP)){
            if(!granted.containsKey(event.getEntity()) || !granted.get(event.getEntity()).equals(AllMobEffects.VULNERABILITY_UP)) {
                event.setCanceled(true);
            }
        }
    }

    public static void grantAndRemove(LivingEntity entity, Holder<MobEffect> effect){
        if(!entity.level().isClientSide()){
            granted.put(entity, effect);
            entity.removeEffect(effect);
            granted.remove(entity);
        }
    }
}
