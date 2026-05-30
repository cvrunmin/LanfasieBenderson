package io.github.cvrunmin.lanfasie.benderson.compat.geckolib.mixin;

import com.geckolib.loading.definition.animation.ActorBoneAnimationKeyframe;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Locale;

//TODO: remove this mixin once BlockBench can export correct value of easing (lowercase, not lowerCamelCase)
@Mixin(ActorBoneAnimationKeyframe.class)
public class ActorBoneAnimationKeyframeMixin {
    @Definition(id = "obj", local = @Local(type = JsonObject.class, name = "obj"))
    @Definition(id = "get", method = "Lcom/google/gson/JsonObject;get(Ljava/lang/String;)Lcom/google/gson/JsonElement;")
    @Definition(id = "getAsString", method = "Lcom/google/gson/JsonElement;getAsString()Ljava/lang/String;")
    @Expression("obj.get('easing').getAsString()")
    @ModifyExpressionValue(method = "lambda$gsonDeserializer$0", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static String mapEasingTypeString(String original){
        return original.toLowerCase(Locale.ROOT);
    }
}
