package io.github.cvrunmin.lanfasie.benderson.content.effects;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class AggroUpMobEffect extends MobEffect {
    public AggroUpMobEffect(MobEffectCategory category, int color) {
        super(category, color, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        if(mob instanceof ServerPlayer player){
            if (player.getInventory().contains(stack -> stack.is(AllItems.PROVOKING_STICK))) {
                return true;
            }
            if(player.containerMenu != null) {
                return player.containerMenu.getCarried().is(AllItems.PROVOKING_STICK);
            }
            return false;
        }
        return false;
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
