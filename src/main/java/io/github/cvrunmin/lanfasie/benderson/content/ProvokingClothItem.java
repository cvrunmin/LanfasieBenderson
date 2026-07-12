package io.github.cvrunmin.lanfasie.benderson.content;

import io.github.cvrunmin.lanfasie.benderson.foundation.IHasEnmity;
import io.github.cvrunmin.lanfasie.benderson.index.AllTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ProvokingClothItem extends Item {
    public ProvokingClothItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
        if(player.getCooldowns().isOnCooldown(itemStack)) return InteractionResult.FAIL;
        if(player.isSpectator() || !target.canAttack(player)) return InteractionResult.PASS;
        if(target instanceof IHasEnmity targetWithEnmity){
            boolean isClientSide = target.level().isClientSide();
            if(!isClientSide){
                var maxEnmity = targetWithEnmity.getMaxEnmity();
                targetWithEnmity.setEnmity(player.getUUID(), maxEnmity + 1000);
                ((ServerLevel) target.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, target.getX(), target.getY(0.5), target.getZ(), 5, target.getBbWidth() / 2, target.getBbHeight() / 2, target.getBbWidth() / 2, 0);
                target.level().playSound(null, target, SoundEvents.ZOMBIE_VILLAGER_CURE, target.getSoundSource(), 1.0f, 2.0f);
            }
            player.getCooldowns().addCooldown(itemStack, 200);
            return InteractionResult.SUCCESS;
        } else if(target instanceof Mob mob && target.is(AllTags.CAN_GET_PROVOKED)){
            if(player.isCreative()) return InteractionResult.PASS;
            if(target.level().isClientSide()) return InteractionResult.SUCCESS_SERVER;
            var oldTarget = mob.getTargetUnchecked();
            mob.setTarget(player);
            if(oldTarget != mob.getTargetUnchecked()) return InteractionResult.PASS;
            ((ServerLevel) target.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, target.getX(), target.getY(0.5), target.getZ(), 5, target.getBbWidth() / 2, target.getBbHeight() / 2, target.getBbWidth() / 2, 0);
            target.level().playSound(null, target, SoundEvents.ZOMBIE_VILLAGER_CURE, target.getSoundSource(), 1.0f, 2.0f);
            player.getCooldowns().addCooldown(itemStack, 200);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
