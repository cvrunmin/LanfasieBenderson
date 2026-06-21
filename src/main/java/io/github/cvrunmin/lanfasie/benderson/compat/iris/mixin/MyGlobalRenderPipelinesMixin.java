package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyGlobalRenderPipelines;
import net.irisshaders.iris.platform.PipelineBuilderStorage;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MyGlobalRenderPipelines.class)
public class MyGlobalRenderPipelinesMixin {
    @Definition(id = "build", method = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;build()Lcom/mojang/blaze3d/pipeline/RenderPipeline;")
    @Definition(id = "ENTITY_SOLID_TRIANGLE_BUILDER", field = "Lio/github/cvrunmin/lanfasie/benderson/foundation/MyGlobalRenderPipelines;ENTITY_SOLID_TRIANGLE_BUILDER:Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;")
    @Expression("ENTITY_SOLID_TRIANGLE_BUILDER.build()")
    @ModifyReceiver(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static RenderPipeline.Builder irisCompat$copyPipeline(RenderPipeline.Builder instance){
        if(((PipelineBuilderAccessor) instance).getLocation().map(id -> id.getNamespace().equals(LanfasieBenderson.MODID)).orElse(false)){
            ((PipelineBuilderStorage) instance).copyPipelineShaderFrom(RenderPipelines.ENTITY_SOLID);
        }
        return instance;
    }
}
