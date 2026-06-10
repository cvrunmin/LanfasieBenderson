package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Optional;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class EventHandlers {
    @SubscribeEvent
    public static void onHurt(LivingIncomingDamageEvent event){
        if(event.getEntity().hasEffect(AllMobEffects.CURSE_NETHER_DOG) && event.getSource().is(DamageTypeTags.IS_FIRE) && !event.getEntity().fireImmune()){
            var maxTakenDamage = event.getEntity().getHealth() - 1;
            var oldDamage = event.getAmount();
            var rand = Optional.of(event.getEntity().level()).map(Level::getRandom).map(RandomSource::fork).orElse(new LegacyRandomSource(System.currentTimeMillis()));
            var multiplier = rand.nextIntBetweenInclusive(16, 48) / 32.0f;
            var damage = Math.max(maxTakenDamage, oldDamage * multiplier);
            event.setAmount(damage);
            if(damage <= 0){
                event.setCanceled(true);
            }
        }
    }
}
