package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyGlobalRenderPipelines;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BendersonRenderer.class)
public class BendersonRendererMixin {

    @Inject(method = "hackStateEnter", at = @At("HEAD"))
    private void irisCompat$injectHackBlockState(int flag, CallbackInfo ci){
        if(flag == 1){
            Object2IntMap<BlockState> blockStateIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
            ImmediateState.isRenderingBEs = true;
            if (blockStateIds != null && ImmediateState.isRenderingLevel) {
                int intId = blockStateIds.applyAsInt(Blocks.END_GATEWAY.defaultBlockState());
                CapturedRenderingState.INSTANCE.setCurrentBlockEntity(intId);
            }
        }
    }

    @Inject(method = "hackStateLeave", at = @At("HEAD"))
    private void irisCompat$injectHackBlockStateEnd(CallbackInfo ci){
        CapturedRenderingState.INSTANCE.setCurrentBlockEntity(0);
        ImmediateState.isRenderingBEs = false;
    }

    @WrapOperation(method = "postRenderPass", at = @At(value = "FIELD", target = "Lio/github/cvrunmin/lanfasie/benderson/content/benderson/BendersonRenderer;PORTAL:Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType irisCompat$renderType(Operation<RenderType> original){
        return Iris.getCurrentPack().isPresent() ? RenderTypes.entitySolid(TheEndPortalRenderer.END_PORTAL_LOCATION) : original.call();
    }

    @WrapOperation(method = "postRenderPass", at = @At(value = "FIELD", target = "Lio/github/cvrunmin/lanfasie/benderson/content/benderson/BendersonRenderer;PORTAL_TRIANGLE:Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType irisCompat$renderTypeTriangle(Operation<RenderType> original){
        return Iris.getCurrentPack().isPresent() ? MyGlobalRenderPipelines.entitySolidTriangle(TheEndPortalRenderer.END_PORTAL_LOCATION) : original.call();
    }

    @Inject(method = "lambda$postRenderPass$3", at = @At("HEAD"), cancellable = true)
    private static void irisCompat$onRenderEnteringPortal(Vector3f vfrom, Vector3f vto, PoseStack.Pose inPose, VertexConsumer buffer, CallbackInfo ci){
        if (!Iris.getCurrentPack().isEmpty()) {
            int overlay = OverlayTexture.NO_OVERLAY;
            int light = LightCoordsUtil.FULL_BRIGHT;
            ci.cancel();
            float progress = SystemTimeUniforms.TIMER.getFrameTimeCounter() * 0.01F % 1.0F;

            for (Direction direction : Direction.values()) {
                float nx = direction.getStepX();
                float ny = direction.getStepY();
                float nz = direction.getStepZ();
                var faceInfo = FaceInfo.fromFacing(direction);
                for (int i = 0; i < 4; i++) {
                    var faceVertex = faceInfo.getVertexInfo(i).select(vfrom, vto);
                    buffer.addVertex(inPose, faceVertex.x(), faceVertex.y(), faceVertex.z())
                            .setColor(0.075F, 0.15F, 0.2F, 1.0F)
                            .setUv(0.0F + (i >= 2 ? 0.2f : 0) + progress, 0.0F + (i == 1 || i == 2 ? 0.2f : 0) + progress)
                            .setOverlay(overlay)
                            .setLight(light)
                            .setNormal(inPose, nx, ny, nz);
                }
            }
        }
    }

    @Inject(method = "lambda$postRenderPass$4", at = @At("HEAD"), cancellable = true)
    private static void irisCompat$onRenderUnforgivingPortal(PoseStack.Pose inPose, VertexConsumer buffer, CallbackInfo ci){
        if (!Iris.getCurrentPack().isEmpty()) {
            int overlay = OverlayTexture.NO_OVERLAY;
            int light = LightCoordsUtil.FULL_BRIGHT;
            ci.cancel();
            float progress = SystemTimeUniforms.TIMER.getFrameTimeCounter() * 0.01F % 1.0F;

            var vertices = List.of(
                    new Vector3f(-2, 0, -2),
                    new Vector3f(-2, 0, 2),
                    new Vector3f(2, 0, 2),
                    new Vector3f(2, 0, -2),
                    new Vector3f(0, 4, 0),
                    new Vector3f(0, -4, 0)
            );
            var uvs = List.of(
                    new Vector2f(0, 0.2f),
                    new Vector2f(0.2f, 0.2f),
                    new Vector2f(0.0f, 0.2f),
                    new Vector2f(0.2f, 0.2f),
                    new Vector2f(0.1f, 0),
                    new Vector2f(0.1f, 0.4f)
            );
            var indices = List.of(
                    new Vector3i(0, 1, 4),
                    new Vector3i(1, 2, 4),
                    new Vector3i(2, 3, 4),
                    new Vector3i(3, 0, 4),
                    new Vector3i(0, 5, 1),
                    new Vector3i(1, 5, 2),
                    new Vector3i(2, 5, 3),
                    new Vector3i(3, 5, 0)
            );
            for (Vector3i index : indices) {
                var aVec = vertices.get(index.y).sub(vertices.get(index.x), new Vector3f());
                var bVec = vertices.get(index.z).sub(vertices.get(index.x), new Vector3f());
                var nVec = aVec.cross(bVec, new Vector3f()).normalize();
                Vector2f uv = uvs.get(index.x);
                buffer.addVertex(inPose, vertices.get(index.x))
                        .setColor(0.075F, 0.15F, 0.2F, 1.0F)
                        .setUv(uv.x + progress, uv.y + progress)
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(inPose, nVec);
                uv = uvs.get(index.y);
                buffer.addVertex(inPose, vertices.get(index.y))
                        .setColor(0.075F, 0.15F, 0.2F, 1.0F)
                        .setUv(uv.x + progress, uv.y + progress)
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(inPose, nVec);
                uv = uvs.get(index.z);
                buffer.addVertex(inPose, vertices.get(index.z))
                        .setColor(0.075F, 0.15F, 0.2F, 1.0F)
                        .setUv(uv.x + progress, uv.y + progress)
                        .setOverlay(overlay)
                        .setLight(light)
                        .setNormal(inPose, nVec);
            }
        }
    }
}
