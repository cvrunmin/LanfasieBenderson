package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.ItemInHandGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

public class MyItemInHandGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends GeoRenderState> extends ItemInHandGeoLayer<T, O, R> {

    public MyItemInHandGeoLayer(EntityRendererProvider.Context context, GeoRenderer renderer, @Nullable String rightHandBoneName, @Nullable String leftHandBoneName) {
        super(context, renderer, rightHandBoneName, leftHandBoneName);
    }

    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
//                poseStack.mulPose(Axis.XN.rotationDegrees(90f));
//                poseStack.translate(0, 0.125f, -0.0625f);

            if (renderState.getOrDefaultGeckolibData(renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false) ? OFFHAND_SHIELD : MAINHAND_SHIELD, false))
                poseStack.translate(0, 0.125, -0.25);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
//                poseStack.mulPose(Axis.XN.rotationDegrees(90f));
//                poseStack.translate(0, 0.125f, -0.0625f);

            if (renderState.getOrDefaultGeckolibData(renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false) ? MAINHAND_SHIELD : OFFHAND_SHIELD, false)) {
                poseStack.translate(0, 0.125, 0.25 - 0.375);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }

        super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
        poseStack.popPose();
    }
}
