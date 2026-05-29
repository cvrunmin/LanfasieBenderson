package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.cvrunmin.lanfasie.benderson.MobEffectRemovalProtector;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectCommands.class)
public class EffectCommandsMixin {
    @WrapOperation(method = "clearEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private static boolean redirectToGrantedRemoveIfNeeded(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original){
        if(effect.is(AllMobEffects.VULNERABILITY_UP) || effect.is(AllMobEffects.AGGRO_UP)){
            MobEffectRemovalProtector.grantAndRemove(instance, effect);
            return true;
        }
        return original.call(instance, effect);
    }
}
