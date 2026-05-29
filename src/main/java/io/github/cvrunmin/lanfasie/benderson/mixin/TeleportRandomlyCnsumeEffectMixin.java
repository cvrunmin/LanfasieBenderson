package io.github.cvrunmin.lanfasie.benderson.mixin;

import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportRandomlyConsumeEffect.class)
public class TeleportRandomlyCnsumeEffectMixin {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void blockTeleportWhenCurseActive(Level level, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir){
        if(user.hasEffect(AllMobEffects.CURSE_END_GUARDIAN)){
            cir.setReturnValue(false);
        }
    }
}
