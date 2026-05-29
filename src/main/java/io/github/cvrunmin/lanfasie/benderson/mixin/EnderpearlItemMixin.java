package io.github.cvrunmin.lanfasie.benderson.mixin;

import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderpearlItem.class)
public class EnderpearlItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void benderson$blockUsageWhenCurseActive(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if(player.hasEffect(AllMobEffects.CURSE_END_GUARDIAN)){
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
