package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonDataTickets;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonWeaponGeoLayer;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.PartialArenaAoePhaseState;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class DelayedAttackMarkerRenderer extends EntityRenderer<DelayedAttackMarker, DelayedAttackMarkerRenderState> {
    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;

    private final BlackCatSmashModel blackCatSmashModel;
    private static final Identifier BLACK_CAT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/cat/cat_all_black.png");
    private QuadInstance quadInstance = new QuadInstance();

    public DelayedAttackMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
        blockModelResolver = context.getBlockModelResolver();
        blackCatSmashModel = new BlackCatSmashModel(context.bakeLayer(ModelLayers.CAT));
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
        state.keypointLifeTick = entity.getKeypointLifeTick();
        state.attackType = entity.getAttackType();
        state.range = entity.getRange();
        state.range2 = entity.getRange2();
        state.direction = entity.getDirectionData();
        if(entity.getAttackType() == DelayedAttackMarker.AttackType.FIREBALL_METEOR){
            this.itemModelResolver.updateForNonLiving(state.itemStackRenderState, new ItemStack(Items.FIRE_CHARGE), ItemDisplayContext.GROUND, entity);
        }else if(entity.getAttackType() == DelayedAttackMarker.AttackType.BLACK_CAT_SMASH){
            state.catRenderState.lightCoords = LightCoordsUtil.FULL_SKY;
            state.catRenderState.partialTick = partialTicks;
        }else if(entity.getAttackType() == DelayedAttackMarker.AttackType.BENDERSON_REMOTE_STACKABLE_METEOR || entity.getAttackType() == DelayedAttackMarker.AttackType.BENDERSON_REMOTE_ECLIPTIC_METEOR){
            blockModelResolver.update(state.blockModelRenderState, AllBlocks.DEEP_LATENT_BLOCK.get().defaultBlockState(), BLOCK_DISPLAY_CONTEXT);
        } else if (entity.getAttackType() == DelayedAttackMarker.AttackType.BENDERSON_REMOTE_SWEEP_PARTIAL_ARENA) {
            this.itemModelResolver.updateForNonLiving(state.itemStackRenderState, new ItemStack(AllItems.CLAYMORE_OF_HEI_POWER.asItem()), ItemDisplayContext.FIXED, entity);
        }
    }

    @Override
    protected int getBlockLightLevel(DelayedAttackMarker entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    protected boolean affectedByCulling(DelayedAttackMarker entity) {
        return false;
    }

    @Override
    public void submit(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        switch (state.attackType){
            case BLACK_CAT_SMASH -> submitBlackCatSmash(state, poseStack, submitNodeCollector, camera);
            case FIREBALL_METEOR -> submitFireball(state, poseStack, submitNodeCollector, camera);
            case BENDERSON_REMOTE_STACKABLE_METEOR -> submitRemoteMeteor(state, poseStack, submitNodeCollector, camera, 3);
            case BENDERSON_REMOTE_ECLIPTIC_METEOR -> submitRemoteMeteor(state, poseStack, submitNodeCollector, camera, 10);
            case BENDERSON_REMOTE_SWEEP_PARTIAL_ARENA -> submitSweepPartialArena(state, poseStack, submitNodeCollector, camera);
            case null, default -> {}
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private void submitBlackCatSmash(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera){
        var lifeTick = state.lifeTick;
        var catMoveTotalTime = (int)((state.range + DelayedAttackMarker.CAT_HALF_DEPTH) * 2 / DelayedAttackMarker.CAT_MOVE_SPEED);
        var waitingTickLen = state.maxLifeTick - DelayedAttackMarker.CAT_LEAVE_TIME - catMoveTotalTime - DelayedAttackMarker.CAT_ENTER_TIME;
        var alpha = 1.0f;
        poseStack.pushPose();
        poseStack.translate(0, 0, -state.range - DelayedAttackMarker.CAT_HALF_DEPTH);
        if(lifeTick < DelayedAttackMarker.CAT_ENTER_TIME){
            var t = lifeTick / DelayedAttackMarker.CAT_ENTER_TIME;
            alpha = (float) (1 - Math.pow(1 - t, 3));
        } else if (lifeTick - DelayedAttackMarker.CAT_ENTER_TIME < waitingTickLen + catMoveTotalTime) {
            var lifeTick1 = lifeTick - DelayedAttackMarker.CAT_ENTER_TIME;
            state.catRenderState.walkAnimationPos = lifeTick1 * 1.5f;
            state.catRenderState.walkAnimationSpeed = 1f;
            if(lifeTick - DelayedAttackMarker.CAT_ENTER_TIME >= waitingTickLen && lifeTick - DelayedAttackMarker.CAT_ENTER_TIME - waitingTickLen < catMoveTotalTime){
                var lifeTick2 = lifeTick - DelayedAttackMarker.CAT_ENTER_TIME - waitingTickLen;
                poseStack.translate(0, 0, lifeTick2 * DelayedAttackMarker.CAT_MOVE_SPEED);
            }
        } else {
            poseStack.translate(0, 0, state.range * 2 + DelayedAttackMarker.CAT_HALF_DEPTH * 2);
            var t = Mth.clamp(1 - (state.maxLifeTick - lifeTick) / DelayedAttackMarker.CAT_LEAVE_TIME, 0, 1);
            alpha = 1 - t;
        }
        poseStack.scale(10f, 10f, 10f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 0));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        submitNodeCollector.submitModel(blackCatSmashModel,
                state.catRenderState,
                poseStack,
                RenderTypes.entityTranslucentCullItemTarget(BLACK_CAT_TEXTURE),
                state.catRenderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                ARGB.colorFromFloat(alpha, 1, 1, 1), null, state.outlineColor, null);
        poseStack.popPose();
    }

    private void submitFireball(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera){
        if(state.maxLifeTick - state.lifeTick < 5) return;
        var tGround = state.maxLifeTick - 8 - state.lifeTick;
        if(tGround > 20) return;
        // TODO: do we need fade in transition?
        // var alpha = Mth.clamp((20 - tGround) / 5.0f, 0, 1);
        poseStack.pushPose();
        poseStack.scale(state.range * 3, state.range * 3, state.range * 3);
        poseStack.mulPose(camera.orientation);
        poseStack.translate(0, 10 * tGround / 20f, 0);
        state.itemStackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    private void submitRemoteMeteor(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float scale){
        if(state.maxLifeTick - state.lifeTick < 5) return;
        float tAll = state.maxLifeTick - 5;
        float tKp = tAll - state.keypointLifeTick;
        float tGround = state.lifeTick;
        float yOffset = state.range2;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, 10f * Mth.clamp(1 - tGround / tKp, 0, 1) - yOffset / scale * Mth.clamp((tGround - tKp) / state.keypointLifeTick, 0, 1), 0);
        poseStack.rotateAround(new Quaternionf().rotationZYX((float) (Math.PI * 30 / 180), (float) (Math.PI * 0.25), 0), 0, 0, 0);
        poseStack.translate(-0.5, -0.5, -0.5);
        state.blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.pushPose();
        poseStack.scale(0.33333f, 0.66667f, 0.33333f);
        poseStack.translate(2, 0.5, 2.5);
        state.blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.33333f, 0.5f);
        poseStack.translate(-0, 0.75, -0.5);
        state.blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.6f, 0.48f);
        poseStack.translate(1.5, -0.62, 0);
        state.blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        poseStack.popPose();
    }

    private void submitSweepPartialArena(DelayedAttackMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera){
        float alpha;
        float remainingLifeTick = state.maxLifeTick - state.lifeTick;
        if(state.lifeTick < 5){
            alpha = (float) Mth.clamp(state.lifeTick / 5f, 0, 1);
        } else if(remainingLifeTick >= 35 && remainingLifeTick <= 40){
            alpha = (float) Mth.clamp(remainingLifeTick - 35 / 5f, 0, 1);
        } else if(remainingLifeTick < 35){
            alpha = 0;
        } else{
            alpha = 1;
        }
        if(alpha >= 0.1){
            float arenaRadius = state.range;
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), state.direction));
            Quaternionf rotationStart = new Quaternionf().rotationTo(new Vector3f(-1, 1, 0).normalize(), new Vector3f(0, 1, 0))
                    .rotateLocalX((float) (-Math.PI * 0.5))
                    .rotateLocalY((float) (-Math.PI * 1 / 3))
                    .rotateY((float) (Math.PI * 1 / 6));
            Quaternionf rotationMiddle = new Quaternionf().rotationTo(new Vector3f(-1, 1, 0).normalize(), new Vector3f(0, 1, 0))
                    .rotateLocalX((float) (-Math.PI * 0.5))
                    .rotateLocalY((float) (-Math.PI))
                    .rotateLocalX((float) (Math.PI * 1 / 18));
            Quaternionf rotationEnd = new Quaternionf().rotationTo(new Vector3f(-1, 1, 0).normalize(), new Vector3f(0, 1, 0))
                    .rotateLocalX((float) (-Math.PI * 0.5))
                    .rotateLocalY((float) (-Math.PI * 5 / 3))
                    .rotateX((float) (Math.PI * 1 / 6))
                    ;
            Quaternionf rotation;
            if(remainingLifeTick <= 50){
                rotation = new Quaternionf();
                var t1 = Math.clamp((50 - remainingLifeTick) / 10.0f, 0, 1);
                var t2 = -(Math.cos(Math.PI * t1) - 1) / 2;
                if(t2 < 0.5){
                    rotationStart.slerp(rotationMiddle, (float) Mth.clamp(t2 / 0.5, 0, 1), rotation);
                }else{
                    rotationMiddle.slerp(rotationEnd, (float) Mth.clamp((t2 - 0.5) / 0.5, 0, 1), rotation);
                }
            }else{
                rotation = rotationStart;
            }
            poseStack.rotateAround(rotation, 0, 5, -2);
            poseStack.translate(0, 5, -2);
            poseStack.scale(arenaRadius, arenaRadius, arenaRadius);
            poseStack.translate(-0.5f, 0.5f, 0);
            BendersonWeaponGeoLayer.customSubmitItemStackState(state.itemStackRenderState, poseStack, submitNodeCollector, quadInstance, state.getPackedLight(), OverlayTexture.NO_OVERLAY, alpha);
            poseStack.popPose();
        }
    }
}
