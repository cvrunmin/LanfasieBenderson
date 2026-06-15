package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Attributes.class)
public class AttributesMixin {
    @Definition(id = "RangedAttribute", type = RangedAttribute.class)
    @Expression("new RangedAttribute(?, ?, ?, ?)")
    @WrapOperation(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static RangedAttribute lanfasie_benderson$replaceWithUnrestrictedIfNeeded(String descriptionId, double defaultValue, double minValue, double maxValue, Operation<RangedAttribute> original){
        if(descriptionId.equals("attribute.name.max_health")){
            if(maxValue < 1e10){
                return original.call(descriptionId, defaultValue, minValue, Double.MAX_VALUE);
            }
        }
        return original.call(descriptionId, defaultValue, minValue, maxValue);
    }
}
