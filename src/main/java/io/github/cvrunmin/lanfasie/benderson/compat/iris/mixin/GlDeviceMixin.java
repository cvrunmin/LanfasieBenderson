package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.compat.iris.IrisCompat;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarkerRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice", priority = 500)
public class GlDeviceMixin {

    @Shadow
    @Final
    private Map<RenderPipeline, GlRenderPipeline> pipelineCache;

    @Shadow
    private GlRenderPipeline compilePipeline(RenderPipeline pipeline, ShaderSource shaderSource) {
        throw new AssertionError();
    }

    @Shadow
    @Final
    private ShaderSource defaultShaderSource;

    @Inject(method = "getOrCompilePipeline", at = @At("HEAD"), cancellable = true)
    private void bypassIrisProgramOverride(RenderPipeline pipeline, CallbackInfoReturnable<GlRenderPipeline> cir){
        if(pipeline.getLocation().getNamespace().equals(LanfasieBenderson.MODID)){
            if(IrisCompat.irisShallNotOverride.contains(pipeline.getLocation())){
                cir.setReturnValue(this.pipelineCache.computeIfAbsent(pipeline,
                        key -> this.compilePipeline(key, this.defaultShaderSource)));
            }
        }
    }
}
