package io.github.cvrunmin.lanfasie.benderson.content.lanfasie;

import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class LanfasieRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LanfasieEntity, R> {
    public LanfasieRenderer(EntityRendererProvider.Context ctx){
        super(ctx, new DefaultedEntityGeoModel<>(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "lanfasie")));
        withRenderLayer(new LuteRenderer<>(ctx, this));
    }

    @Override
    public void addRenderData(LanfasieEntity animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(LanfasieDataTickets.ANIMATE_STATE, animatable.getAnimateState());
    }

    static class LuteRenderer<O, R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<LanfasieEntity, O, R> {
        protected ItemModelResolver itemModelResolver;
        public LuteRenderer(EntityRendererProvider.Context context, GeoRenderer<LanfasieEntity, O, R> renderer) {
            super(renderer);
            itemModelResolver = context.getItemModelResolver();
        }

        @Override
        public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
            if(!renderPassInfo.willRender()) return;
            final BakedGeoModel model = renderPassInfo.model();
            model.getBone("bodyPart").ifPresent(bone -> {
                consumer.accept(bone, ((renderPassInfo1, bone1, renderTasks) -> {
                    sumbitItemStack(renderPassInfo1.poseStack(), bone1, renderPassInfo1.renderState(), renderTasks, renderPassInfo1.packedLight());
                }));
            });
        }

        private void sumbitItemStack(PoseStack poseStack, GeoBone bone, R renderState, SubmitNodeCollector submitNodeCollector, int packedLight) {
            poseStack.pushPose();
            poseStack.translate(0, 0.3, -0.25);
            poseStack.rotateAround(new Quaternionf().rotationZ((float) (Math.PI * 15 / 180.0)), 0, 0, 0);
            RenderUtil.createRenderStateForItem(AllItems.BARDS_LUTE.toStack(), itemModelResolver, ItemDisplayContext.FIXED)
                    .submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            poseStack.popPose();
        }
    }
}
