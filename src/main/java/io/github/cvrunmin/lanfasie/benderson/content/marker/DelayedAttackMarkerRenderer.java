package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class DelayedAttackMarkerRenderer extends EntityRenderer<DelayedAttackMarker, DelayedAttackMarkerRenderState> {
    public DelayedAttackMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public DelayedAttackMarkerRenderState createRenderState() {
        return new DelayedAttackMarkerRenderState();
    }

    @Override
    public void extractRenderState(DelayedAttackMarker entity, DelayedAttackMarkerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

    }

    @Override
    public void submit(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {


        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
