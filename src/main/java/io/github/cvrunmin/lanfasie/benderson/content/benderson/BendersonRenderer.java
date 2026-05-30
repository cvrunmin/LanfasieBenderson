package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.AnticalabrumModel;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class BendersonRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<Benderson, R> {
    public BendersonRenderer(EntityRendererProvider.Context ctx){
        super(ctx, AllEntityTypes.BENDERSON.get());
        withRenderLayer(new BendersonWeaponGeoLayer<>(ctx, this));
    }

    @Override
    public void addRenderData(Benderson animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(BendersonDataTickets.ANIMATE_STATE, animatable.getAnimateState());
    }

    @Override
    public void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.postRenderPass(renderPassInfo, renderTasks);
        var controller = Optional.ofNullable(renderPassInfo.getGeckolibData(DataTickets.ANIMATABLE_MANAGER)).map(AnimatableManager::getAnimationControllers).map(map -> map.get("Special Attack")).orElse(null);
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
                    renderTasks.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(AnticalabrumModel.ANTICALABRUM_TEXTURE), (inPose, builder) -> {
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
    }
}
