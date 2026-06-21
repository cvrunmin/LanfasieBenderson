package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.mojang.blaze3d.systems.RenderPassBackend;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.compat.iris.IrisCompat;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarkerRenderer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlCommandEncoder")
public class GlCommandEncoderMixin {

    @Inject(method = "trySetup", at = @At("RETURN"))
    private void irisCompat$stealFbo(@Coerce() RenderPassBackend renderPass, Collection<String> dynamicUniforms, CallbackInfoReturnable<Boolean> cir){
        if(IrisApi.getInstance().isShaderPackInUse()){
            Identifier location = ((GlRenderPassAccessor) renderPass).getPipeline().info().getLocation();
            if(location.getNamespace().equals(LanfasieBenderson.MODID)){
                if (IrisCompat.stolenFbos.containsKey(location)) {
                    var fbos = IrisCompat.stolenFbos.get(location);
                    var pipeline = Iris.getPipelineManager().getPipelineNullable();
                    if(pipeline instanceof IrisRenderingPipeline irisRenderingPipeline){
                        if(irisRenderingPipeline.isBeforeTranslucent){
                            fbos.getA().bind();
                        }else{
                            fbos.getB().bind();
                        }
                    }
                }
            }
        }
    }
}
