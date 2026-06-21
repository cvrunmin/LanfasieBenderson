package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.cvrunmin.lanfasie.benderson.foundation.HeiTideSkyboxRenderer;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeiTideSkyboxRenderer.class)
public class HeiTideSkyboxRendererMixin {

    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lio/github/cvrunmin/lanfasie/benderson/foundation/HeiTideSkyboxRenderer;renderSkyDisc(I)V"))
    private void irisCompat$forceShaderSkyFirstIfNeeded(HeiTideSkyboxRenderer instance, int skyColor, Operation<Void> original, @Local(name = "skyRenderer") SkyRenderer skyRenderer){
        if(IrisApi.getInstance().isShaderPackInUse()){
            skyRenderer.renderSkyDisc(skyColor);
        }
        original.call(instance, skyColor);
    }

    @Inject(method = "shouldOverrideFog", at = @At("HEAD"), cancellable = true)
    private void irisCompat$overrideFog(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(IrisApi.getInstance().isShaderPackInUse());
    }
}
