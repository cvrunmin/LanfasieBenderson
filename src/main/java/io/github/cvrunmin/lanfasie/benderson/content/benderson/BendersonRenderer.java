package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.AnticalabrumModel;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.ArenaEnteringPhaseState;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.ElevateToExtremeState;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.KnockbackFromCenterPhaseState;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.SummonAnticalabrumPhaseState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class BendersonRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Benderson, R> {

    private static final RenderPipeline.Snippet END_PORTAL_TRIANGLE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader("core/rendertype_end_portal")
            .withFragmentShader("core/rendertype_end_portal")
            .withSampler("Sampler0")
            .withSampler("Sampler1")
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLES)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .buildSnippet();

    private static final RenderPipeline PORTAL_PIPELINE = RenderPipeline.builder(RenderPipelines.END_PORTAL_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/deep_latent_portal"))
            .withShaderDefine("PORTAL_LAYERS", 15)
            .withCull(false).build();

    private static final RenderPipeline PORTAL_TRIANGLE_PIPELINE = RenderPipeline.builder(END_PORTAL_TRIANGLE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/deep_latent_portal_triangle"))
            .withShaderDefine("PORTAL_LAYERS", 15)
            .withCull(false).build();

    private static final RenderType PORTAL = RenderType.create(
            "deep_latent_portal",
            RenderSetup.builder(PORTAL_PIPELINE)
            .withTexture("Sampler0",AbstractEndPortalRenderer.END_SKY_LOCATION)
            .withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION)
            .createRenderSetup()
    );

    private static final RenderType PORTAL_TRIANGLE = RenderType.create(
            "deep_latent_portal_triangle",
            RenderSetup.builder(PORTAL_TRIANGLE_PIPELINE)
            .withTexture("Sampler0",AbstractEndPortalRenderer.END_SKY_LOCATION)
            .withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION)
            .createRenderSetup()
    );

    public BendersonRenderer(EntityRendererProvider.Context ctx){
        super(ctx, new BendersonGeoModel());
        withRenderLayer(new BendersonWeaponGeoLayer<>(ctx, this));
    }

    @Override
    public void addRenderData(Benderson animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(BendersonDataTickets.ANIMATE_STATE, animatable.getAnimateState());
        renderState.addGeckolibData(BendersonDataTickets.BODY_STATE, animatable.getBodyState());
        if(animatable.getBodyState() == Benderson.BodyState.ENTRANCE){
            var arenaCenter = animatable.clientGetCombatArenaCenter();
            renderState.x = arenaCenter.x;
            renderState.y = arenaCenter.y + 10;
            renderState.z = arenaCenter.z - animatable.getArenaRadius() - 1;
            renderState.scale = 24;
        }
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        if(renderPassInfo.getGeckolibData(BendersonDataTickets.BODY_STATE) == Benderson.BodyState.ENTRANCE){
            snapshots.ifPresent("rightLeg", bone -> bone.skipRender(true));
            snapshots.ifPresent("rightLowerLeg", bone -> bone.skipRender(true));
            snapshots.ifPresent("leftLeg", bone -> bone.skipRender(true));
            snapshots.ifPresent("leftLowerLeg", bone -> bone.skipRender(true));
            snapshots.ifPresent("leftArm", bone -> bone.skipRender(true));
            snapshots.ifPresent("leftArmRotator", bone -> bone.skipRender(true));
            snapshots.ifPresent("leftForearm", bone -> bone.skipRender(true));
            snapshots.ifPresent("head", bone -> bone.skipRender(true));
            snapshots.ifPresent("hat", bone -> bone.skipRender(true));
            snapshots.ifPresent("body1", bone -> bone.skipRender(true));
            var performController = Optional.ofNullable(renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_MANAGER)).map(AnimatableManager::getAnimationControllers).map(map -> map.get("Special Performing")).orElse(null);
            if(performController != null){
                var tSec = performController.getCurrentTimelineTime();
                if(tSec < 0.5 || tSec >= 7){
                    snapshots.ifPresent("rightArm", bone -> {
                        bone.skipRender(true);
                        bone.skipChildrenRender(true);
                    });
                }
            }
        } else if (renderPassInfo.getGeckolibData(BendersonDataTickets.BODY_STATE) == Benderson.BodyState.UNVEILED) {
            snapshots.ifPresent("hat", bone -> bone.skipRender(true));
        }
        if(Objects.equals(renderPassInfo.getGeckolibData(BendersonDataTickets.ANIMATE_STATE), KnockbackFromCenterPhaseState.ANIMATE_STATE_LOOP)){
            snapshots.ifPresent("root", bone -> {
                bone.skipRender(true);
                bone.skipChildrenRender(true);
            });
        }
    }

    @Override
    protected boolean affectedByCulling(Benderson entity) {
        if(entity.getBodyState().isTransition()) return false;
        if(entity.isShouldHideBoundingBox()) return false;
        return true;
    }

    @Override
    public void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector submitNodeCollector) {
        super.postRenderPass(renderPassInfo, submitNodeCollector);
        var controller = Optional.ofNullable(renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_MANAGER)).map(AnimatableManager::getAnimationControllers).map(map -> map.get("Special Attack")).orElse(null);
        var performController = Optional.ofNullable(renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_MANAGER)).map(AnimatableManager::getAnimationControllers).map(map -> map.get("Special Performing")).orElse(null);
        if(controller != null && Objects.equals(renderPassInfo.renderState().getGeckolibData(BendersonDataTickets.ANIMATE_STATE), SummonAnticalabrumPhaseState.ANIMATE_STATE_START)){
            var tSec = controller.getCurrentTimelineTime();
            if(tSec < 1){
                var model = Minecraft.getInstance().getModelManager().getStandaloneModel(AnticalabrumModel.MODEL_KEY);
                if(model != null){
                    var alphaT = Mth.clamp((tSec) / 0.25f, 0, 1);
                    var rotT = Mth.clamp((tSec - 0.25f) / 0.25f, 0, 1);
                    var flyT = Mth.clamp((tSec - 0.5f) / 0.5f, 0, 1);
                    var quadInstance = new QuadInstance();
                    var poseStack = renderPassInfo.poseStack();
                    poseStack.pushPose();
                    float rotationYaw = renderPassInfo.renderState().getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));
                    poseStack.translate(0, 10 * Math.pow(flyT, 3), 0);
                    poseStack.translate(renderPassInfo.renderState().boundingBoxWidth * 0, renderPassInfo.renderState().boundingBoxHeight * 0.3333333f, renderPassInfo.renderState().boundingBoxWidth * 1f);
                    poseStack.rotateAround(new Quaternionf().rotationZ((float) (-Math.PI * (1 - Math.pow(1 - rotT, 3)))), 0, 1, 0);
                    poseStack.mulPose(new Quaternionf().rotationZ((float) (-Math.PI * 0.5)));
                    var quads = model.getQuadsByState(Anticalabrum.AnticalabrumType.EMPTY);
                    submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(AnticalabrumModel.ANTICALABRUM_TEXTURE), (inPose, builder) -> {
                        quadInstance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
                        quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
                        quadInstance.setColor(ARGB.colorFromFloat((float) (1 - Math.pow(1 - alphaT, 3)), 1, 1, 1));
                        for (BakedQuad quad : quads) {
                            builder.putBakedQuad(inPose, quad, quadInstance);
                        }
                    });
                    poseStack.popPose();
                }
            }
        }
        if(renderPassInfo.getGeckolibData(BendersonDataTickets.BODY_STATE) == Benderson.BodyState.ENTRANCE
                && performController != null
                && Objects.equals(renderPassInfo.renderState().getGeckolibData(BendersonDataTickets.ANIMATE_STATE), ArenaEnteringPhaseState.ANIMATE_STATE_START)){
            var tSec = performController.getCurrentTimelineTime();
            if(tSec < 8){
                float gateOpenScale;
                float gateOpenZScale;
                if(tSec < 1){
                    gateOpenScale = (float) (1 - Math.pow(1 - tSec, 3));
                    gateOpenZScale = 1;
                }
                else if(tSec >= 7){
                    gateOpenScale = 1 - Mth.clamp((float) (Math.pow((tSec - 7) / 0.5, 3)), 0, 1);
                    gateOpenZScale = 1 - Mth.clamp((float) (Math.pow((tSec - 7) / 0.5, 3)), 0, 1);
                }
                else{
                    gateOpenScale = 1;
                    gateOpenZScale = 1;
                }
                var poseStack = renderPassInfo.poseStack();
                poseStack.pushPose();
                var vfrom = new Vector3f(-24, -10 * gateOpenScale, -5 * gateOpenZScale);
                var vto = new Vector3f(24, 10 * gateOpenScale, 0);
                submitNodeCollector.submitCustomGeometry(poseStack, PORTAL, (inPose, buffer) -> {
                    for (FaceInfo faceInfo : FaceInfo.values()) {
                        for (int i = 0; i < 4; i++) {
                            buffer.addVertex(inPose, faceInfo.getVertexInfo(i).select(vfrom, vto));
                        }
                    }
                });
                poseStack.popPose();
            }
        }
        if(renderPassInfo.getGeckolibData(BendersonDataTickets.BODY_STATE) == Benderson.BodyState.TRANSITION_UNFORGIVEN
                && performController != null
                && Objects.equals(renderPassInfo.renderState().getGeckolibData(BendersonDataTickets.ANIMATE_STATE), ElevateToExtremeState.ANIMATE_STATE_P1)){
            var tSec = performController.getCurrentTimelineTime();
            if(tSec >= 2){
                var rootY = Mth.clampedLerp((tSec - 0.67) / (3.0-0.67), 0, 5);

                var poseStack = renderPassInfo.poseStack();
                poseStack.pushPose();
                poseStack.last().set(renderPassInfo.getModelRenderMatrixPose());
                poseStack.translate(0, rootY + 1.0f, 0);
                var scale = (float) Mth.clamp((tSec - 2) / 2.8, 0, 1);
                poseStack.scale(scale, scale, scale);
                submitNodeCollector.submitCustomGeometry(poseStack, PORTAL_TRIANGLE, (inPose, buffer) -> {
                    buffer.addVertex(inPose, -2, 0, -2).addVertex(inPose, -2, 0, 2).addVertex(inPose, 0, 4, 0);
                    buffer.addVertex(inPose, -2, 0, 2).addVertex(inPose, 2, 0, 2).addVertex(inPose, 0, 4, 0);
                    buffer.addVertex(inPose, 2, 0, 2).addVertex(inPose, 2, 0, -2).addVertex(inPose, 0, 4, 0);
                    buffer.addVertex(inPose, 2, 0, -2).addVertex(inPose, -2, 0, -2).addVertex(inPose, 0, 4, 0);
                    buffer.addVertex(inPose, -2, 0, -2).addVertex(inPose, 0, -4, 0).addVertex(inPose, -2, 0, 2);
                    buffer.addVertex(inPose, -2, 0, 2).addVertex(inPose, 0, -4, 0).addVertex(inPose, 2, 0, 2);
                    buffer.addVertex(inPose, 2, 0, 2).addVertex(inPose, 0, -4, 0).addVertex(inPose, 2, 0, -2);
                    buffer.addVertex(inPose, 2, 0, -2).addVertex(inPose, 0, -4, 0).addVertex(inPose, -2, 0, -2);
                });
                poseStack.popPose();
            }
        }
    }
}
