package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @WrapOperation(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void benderson$applyHydroDreamerCurse(FoodData instance, FoodProperties foodProperties, Operation<Void> original, @Local Player player){
        if(player.hasEffect(AllMobEffects.CURSE_HYDRO_DREAMER)){
            instance.setSaturation(Mth.clamp(foodProperties.saturation() + instance.getSaturationLevel(), 0.0F, 20f));
            instance.setFoodLevel((int) Mth.clamp(foodProperties.nutrition() * 0.25f + instance.getFoodLevel(), 0.0F, 20f));
        }else{
            original.call(instance, foodProperties);
        }
    }
}
