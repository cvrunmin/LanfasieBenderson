package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DelayedAttackMarkerRenderer extends EntityRenderer<DelayedAttackMarker, DelayedAttackMarkerRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;

    public DelayedAttackMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
        blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public DelayedAttackMarkerRenderState createRenderState() {
        return new DelayedAttackMarkerRenderState();
    }

    @Override
    public void extractRenderState(DelayedAttackMarker entity, DelayedAttackMarkerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.lifeTick = entity.getLifeTick() + partialTicks;
        state.maxLifeTick = entity.getMaxLifeTick();
        state.attackType = entity.getAttackType();
        state.range = entity.getRange();
        state.range2 = entity.getRange2();
        if(entity.getAttackType() == DelayedAttackMarker.AttackType.FIREBALL_METEOR){
            this.itemModelResolver.updateForNonLiving(state.itemStackRenderState, new ItemStack(Items.FIRE_CHARGE), ItemDisplayContext.GROUND, entity);
        }
    }

    @Override
    protected int getBlockLightLevel(DelayedAttackMarker entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void submit(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        switch (state.attackType){
            case BLACK_CAT_SMASH -> submitBlackCatSmash(state, poseStack, submitNodeCollector, camera);
            case FIREBALL_METEOR -> submitFireball(state, poseStack, submitNodeCollector, camera);
            case null, default -> {}
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private void submitBlackCatSmash(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera){
        var lifeTick = state.lifeTick;
        var catMoveTotalTime = (int)((state.range + DelayedAttackMarker.CAT_HALF_DEPTH * 2) * DelayedAttackMarker.CAT_MOVE_SPEED);
        var waitingTickLen = state.maxLifeTick - DelayedAttackMarker.CAT_LEAVE_TIME - catMoveTotalTime - DelayedAttackMarker.CAT_ENTER_TIME;
        if(lifeTick < DelayedAttackMarker.CAT_ENTER_TIME){
            var t = lifeTick / DelayedAttackMarker.CAT_ENTER_TIME;
        } else if (lifeTick - DelayedAttackMarker.CAT_ENTER_TIME < waitingTickLen) {
            var lifeTick1 = lifeTick - DelayedAttackMarker.CAT_ENTER_TIME;
        } else if(lifeTick - DelayedAttackMarker.CAT_ENTER_TIME - waitingTickLen < catMoveTotalTime){
            var lifeTick1 = lifeTick - DelayedAttackMarker.CAT_ENTER_TIME - waitingTickLen;
        } else {
            var t = Mth.clamp(1 - (state.maxLifeTick - lifeTick) / DelayedAttackMarker.CAT_LEAVE_TIME, 0, 1);
        }
    }

    private void submitFireball(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera){
        if(state.maxLifeTick - state.lifeTick < 5) return;
        var tGround = state.maxLifeTick - 9 - state.lifeTick;
        if(tGround > 20) return;
        // TODO: do we need fade in transition?
        // var alpha = Mth.clamp((20 - tGround) / 5.0f, 0, 1);
        poseStack.pushPose();
        poseStack.scale(state.range, state.range, state.range);
        poseStack.mulPose(camera.orientation);
        poseStack.translate(0, 10 * tGround / 20f, 0);
        state.itemStackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
