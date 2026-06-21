package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.google.common.collect.ImmutableSet;
import io.github.cvrunmin.lanfasie.benderson.compat.iris.IrisCompat;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarkerRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.HeiTideSkyboxRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyGlobalRenderPipelines;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.targets.RenderTargets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Pair;

@Mixin(IrisRenderingPipeline.class)
public class IrisRenderPipelineMixin {
    @Shadow
    @Final
    private RenderTargets renderTargets;

    @Shadow
    @Final
    private ImmutableSet<Integer> flippedAfterTranslucent;

    @Shadow
    @Final
    private ImmutableSet<Integer> flippedAfterPrepare;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void lanfasieIrisCompat$stealFbo(ProgramSet programSet, CallbackInfo ci){
        IrisCompat.stolenFbos.put(MyGlobalRenderPipelines.ISOLATED_SKY.getLocation(),
                new Pair<>(this.renderTargets.createGbufferFramebuffer(this.flippedAfterPrepare, new int[]{0}),
                        this.renderTargets.createGbufferFramebuffer(this.flippedAfterTranslucent, new int[]{0}))
                );
        IrisCompat.stolenFbos.put(MyGlobalRenderPipelines.ISOLATED_SKY_TRANSLUCENT.getLocation(),
                new Pair<>(this.renderTargets.createGbufferFramebuffer(this.flippedAfterPrepare, new int[]{0}),
                        this.renderTargets.createGbufferFramebuffer(this.flippedAfterTranslucent, new int[]{0}))
                );
        IrisCompat.stolenFbos.put(HeiTideSkyboxRenderer.EYES.getLocation(),
                new Pair<>(this.renderTargets.createGbufferFramebuffer(this.flippedAfterPrepare, new int[]{0}),
                        this.renderTargets.createGbufferFramebuffer(this.flippedAfterTranslucent, new int[]{0}))
                );
        IrisCompat.stolenFbos.put(TargetMarkerRenderer.ATTACK_TARGET_MARKER.getLocation(),
                new Pair<>(this.renderTargets.createGbufferFramebuffer(this.flippedAfterPrepare, new int[]{0}),
                        this.renderTargets.createGbufferFramebuffer(this.flippedAfterTranslucent, new int[]{0}))
                );
        IrisCompat.stolenFbos.put(TargetMarkerRenderer.ATTACK_TARGET_MARKER_TRIANGLE_STRIP.getLocation(),
                new Pair<>(this.renderTargets.createGbufferFramebuffer(this.flippedAfterPrepare, new int[]{0}),
                        this.renderTargets.createGbufferFramebuffer(this.flippedAfterTranslucent, new int[]{0})));
    }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void lanfasieIrisCompat$releaseStolenFbo(CallbackInfo ci){
        IrisCompat.stolenFbos.clear();
    }
}
