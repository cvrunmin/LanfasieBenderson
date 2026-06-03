package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.AnticalabrumModel;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.ArenaEnteringPhaseState;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class BendersonRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<Benderson, R> {

    private static final RenderPipeline PORTAL_PIPELINE = RenderPipeline.builder(RenderPipelines.END_PORTAL_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/deep_latent_portal"))
            .withShaderDefine("PORTAL_LAYERS", 15)
            .withCull(false).build();

    private static final RenderType PORTAL = RenderType.create(
            "deep_latent_portal",
            RenderSetup.builder(PORTAL_PIPELINE)
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
        }
    }

    @Override
    protected boolean affectedByCulling(Benderson entity) {
        return entity.getBodyState() == Benderson.BodyState.DEEP_LATENT || entity.getBodyState() == Benderson.BodyState.UNVEILED || entity.getBodyState() == Benderson.BodyState.UNFORGIVEN;
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
                if(tSec < 1){
                    gateOpenScale = (float) (1 - Math.pow(1 - tSec, 3));
                }
                else if(tSec >= 7){
                    gateOpenScale = 1 - Mth.clamp((float) (Math.pow((tSec - 7) / 0.5, 3)), 0, 1);
                }
                else{
                    gateOpenScale = 1;
                }
                var poseStack = renderPassInfo.poseStack();
                poseStack.pushPose();
                var vfrom = new Vector3f(-24, -10 * gateOpenScale, -5);
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
    }
}
