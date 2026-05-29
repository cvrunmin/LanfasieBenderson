package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BendersonWeaponGeoLayer<O, R extends GeoRenderState> extends BlockAndItemGeoLayer<Benderson, O, R> {
    private final Lazy<ItemStack> itemStack;

    public BendersonWeaponGeoLayer(EntityRendererProvider.Context context, GeoRenderer<Benderson, O, R> renderer) {
        super(context, renderer);
        this.itemStack = Lazy.of(() -> new ItemStack(AllItems.SWORD_OF_DAWNWAITER_TAINTED));
    }

    @Override
    protected List<RenderData> getRelevantBones(Benderson animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        return List.of(RenderData.item("rightHand",
                ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                RenderUtil.createRenderStateForItem(itemStack.get(), this.itemModelResolver, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, animatable)));
    }

    @Override
    public void addRenderData(Benderson animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        final List<RenderData> contents = getRelevantBones(animatable, relatedObject, renderState, partialTick);

        if (!contents.isEmpty()) {
            renderState.addGeckolibData(BlockAndItemGeoLayer.CONTENTS, contents);
        }
    }

    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);
        }

        super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
        poseStack.popPose();
    }
}
