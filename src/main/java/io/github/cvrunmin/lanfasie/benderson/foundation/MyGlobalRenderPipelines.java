package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class MyGlobalRenderPipelines {

    public static final RenderPipeline ISOLATED_SKY =
            RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/isolated_sky"))
                    .withVertexShader("core/sky")
                    .withFragmentShader("core/sky")
                    .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLE_FAN)
                    .build();

    public static final RenderPipeline ISOLATED_SKY_TRANSLUCENT =
            RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/isolated_sky_translucent"))
                    .withVertexShader(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "sky_translucent"))
                    .withFragmentShader(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "sky_translucent"))
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLE_FAN)
                    .build();

    // split builder to allow mixin for Iris
    private static final RenderPipeline.Builder ENTITY_SOLID_TRIANGLE_BUILDER = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/entity_solid_triangle"))
            .withSampler("Sampler1");

    public static final RenderPipeline ENTITY_SOLID_TRIANGLE = ENTITY_SOLID_TRIANGLE_BUILDER.build();

    private static final Function<Identifier, RenderType> ENTITY_SOLID = Util.memoize(
    texture -> {
        RenderSetup state = RenderSetup.builder(ENTITY_SOLID_TRIANGLE)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
        return RenderType.create("entity_solid_triangle", state);
    }
    );

    public static RenderType entitySolidTriangle(Identifier texture) {
        return ENTITY_SOLID.apply(texture);
    }
}
