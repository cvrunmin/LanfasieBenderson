package io.github.cvrunmin.lanfasie.benderson.content.anticalabrum;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AnticalabrumRenderer extends EntityRenderer<Anticalabrum, AnticalabrumRenderState> {
    private static final Vector3fc UP = new Vector3f(0, 1, 0);
    private final QuadInstance quadInstance = new QuadInstance();

    public AnticalabrumRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public AnticalabrumRenderState createRenderState() {
        return new AnticalabrumRenderState();
    }

    @Override
    public void extractRenderState(Anticalabrum entity, AnticalabrumRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.type = entity.getAnticalabrumType();
        state.rawLifeTick = entity.getLifeTick();
        state.lifeTick = entity.getLifeTick() >= 0 ? entity.getLifeTick() + partialTicks : 15 + entity.getLifeTick() + partialTicks;
        state.orientation = entity.getSwordOrientation();
    }

    @Override
    public void submit(AnticalabrumRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        var model = Minecraft.getInstance().getModelManager().getStandaloneModel(AnticalabrumModel.MODEL_KEY);
        if(model != null){
            var quads = model.getQuadsByState(state.type);
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotationTo(UP, state.orientation));
            if(state.rawLifeTick < 0){
                var t = state.lifeTick / 16;
                poseStack.translate(0, 25 * (1 - t * t * t), 0);
            }
            poseStack.mulPose(new Quaternionf().rotationZ((float) (-Math.PI * 0.5)));
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(AnticalabrumModel.ANTICALABRUM_TEXTURE), (inPose, builder) -> {
                this.quadInstance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
                this.quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
                this.quadInstance.setColor(0xffffffff);
                for (BakedQuad quad : quads) {
                    builder.putBakedQuad(inPose, quad, this.quadInstance);
                }
            });
            poseStack.popPose();
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
