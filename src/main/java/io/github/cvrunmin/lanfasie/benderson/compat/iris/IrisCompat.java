package io.github.cvrunmin.lanfasie.benderson.compat.iris;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarkerRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.HeiTideSkyboxRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyGlobalRenderPipelines;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.minecraft.resources.Identifier;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.HashSet;

public class IrisCompat {
    public static final HashMap<Identifier, Pair<GlFramebuffer, GlFramebuffer>> stolenFbos = new HashMap<>();
    public static final HashSet<Identifier> irisShallNotOverride = new HashSet<>();

    static void compat(){
        irisShallNotOverride.add(MyGlobalRenderPipelines.ISOLATED_SKY.getLocation());
        irisShallNotOverride.add(MyGlobalRenderPipelines.ISOLATED_SKY_TRANSLUCENT.getLocation());
        irisShallNotOverride.add(HeiTideSkyboxRenderer.EYES.getLocation());
//        irisShallNotOverride.add(TargetMarkerRenderer.ATTACK_TARGET_MARKER.getLocation());
//        irisShallNotOverride.add(TargetMarkerRenderer.ATTACK_TARGET_MARKER_TRIANGLE_STRIP.getLocation());
        IrisApi.getInstance().assignPipeline(MyGlobalRenderPipelines.ISOLATED_SKY, IrisProgram.SKY_BASIC);
        IrisApi.getInstance().assignPipeline(MyGlobalRenderPipelines.ISOLATED_SKY_TRANSLUCENT, IrisProgram.SKY_BASIC);
//        IrisApi.getInstance().assignPipeline(MyGlobalRenderPipelines.ENTITY_SOLID_TRIANGLE, IrisProgram.ENTITIES);
        IrisApi.getInstance().assignPipeline(BendersonRenderer.PORTAL_PIPELINE, IrisProgram.BLOCK);
        IrisApi.getInstance().assignPipeline(BendersonRenderer.PORTAL_TRIANGLE_PIPELINE, IrisProgram.BLOCK);
        IrisApi.getInstance().assignPipeline(TargetMarkerRenderer.ATTACK_TARGET_MARKER, IrisProgram.ENTITIES_TRANSLUCENT);
        IrisApi.getInstance().assignPipeline(TargetMarkerRenderer.ATTACK_TARGET_MARKER_TRIANGLE_STRIP, IrisProgram.ENTITIES_TRANSLUCENT);
        IrisApi.getInstance().assignPipeline(HeiTideSkyboxRenderer.EYES, IrisProgram.SKY_TEXTURED);
    }
}
