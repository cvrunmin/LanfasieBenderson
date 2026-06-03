package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.cvrunmin.lanfasie.benderson.content.effects.ISaturationBreaker;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(FoodData.class)
public class FoodDataMixin implements ISaturationBreaker {
    private boolean saturationLimitBreak;

    @Definition(id = "foodLevel", field = "Lnet/minecraft/world/food/FoodData;foodLevel:I")
    @Expression("(float) this.foodLevel")
    @ModifyExpressionValue(method = "add",
            at = @At("MIXINEXTRAS:EXPRESSION"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/food/FoodData;foodLevel:I", opcode = 181),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/food/FoodData;saturationLevel:F", opcode = 181)
            )
    )
    private float lanfasie_benderson$overrideSaturationCap(float original){
        if(saturationLimitBreak) return 20.0f;
        return original;
    }

    @Override
    public boolean isSaturationLimitBroken() {
        return saturationLimitBreak;
    }

    @Override
    public void setSaturationLimitBreak(boolean flag) {
        saturationLimitBreak = flag;
    }
}
