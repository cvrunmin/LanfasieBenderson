package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.ArenaEnteringPhaseState;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import io.github.cvrunmin.lanfasie.benderson.mixin.ItemLayerRenderStateAccessor;
import io.github.cvrunmin.lanfasie.benderson.mixin.ItemStackRenderStateAccessor;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BendersonWeaponGeoLayer<O, R extends GeoRenderState> extends BlockAndItemGeoLayer<Benderson, O, R> {
    private final Lazy<ItemStack> itemStack;

    private final QuadInstance quadInstance = new QuadInstance();

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
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector submitNodeCollector, int packedLight) {
        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);
        }
        var performController = Optional.ofNullable(renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER)).map(AnimatableManager::getAnimationControllers).map(map -> map.get("Special Performing")).orElse(null);
        if(performController != null && Objects.equals(renderState.getGeckolibData(BendersonDataTickets.ANIMATE_STATE), ArenaEnteringPhaseState.ANIMATE_STATE_START)){
            var tSec= performController.getCurrentTimelineTime();
            if(tSec > 1.25 && tSec < 6.5){
                float whiteOverlayProgress;
                if(tSec < 1.75){
                    whiteOverlayProgress = 1-(float) Math.pow((1 - (tSec - 1.25) / 0.5f), 3);
                }else if(tSec > 6.0){
                    whiteOverlayProgress = ((float) Math.pow((1 - (tSec - 6) / 0.5f), 3));
                }else{
                    whiteOverlayProgress = 1;
                }
                customSubmitItemStackState(stackState, poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, whiteOverlayProgress);
            }
        }
        else{
            stackState.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0);
        }
        poseStack.popPose();
    }

    private void customSubmitItemStackState(ItemStackRenderState stackState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int overlay, float whiteOverlayProgress){
        for (int i = 0; i < ((ItemStackRenderStateAccessor) stackState).getActiveLayerCount(); i++) {
            var layerState = ((ItemStackRenderStateAccessor) stackState).getLayers()[i];
            poseStack.pushPose();
            ((ItemLayerRenderStateAccessor) layerState).invokeApplyTransform(poseStack.last());
            if (((ItemLayerRenderStateAccessor) layerState).getSpecialRenderer() != null) {
                ((ItemLayerRenderStateAccessor) layerState).getSpecialRenderer()
                        .submit(
                                ((ItemLayerRenderStateAccessor) layerState).getArgumentForSpecialRendering(),
                                poseStack,
                                submitNodeCollector,
                                packedLight,
                                overlay,
                                ((ItemLayerRenderStateAccessor) layerState).getFoilType() != ItemStackRenderState.FoilType.NONE,
                                0
                        );
            }else{
                int[] tints = ((ItemLayerRenderStateAccessor) layerState).getTintLayers() != null ? ((ItemLayerRenderStateAccessor) layerState).getTintLayers().toArray(ItemStackRenderState.LayerRenderState.EMPTY_TINTS) : ItemStackRenderState.LayerRenderState.EMPTY_TINTS;
                for (BakedQuad quad : ((ItemLayerRenderStateAccessor) layerState).getQuads()) {
                    BakedQuad.MaterialInfo material = quad.materialInfo();
                    RenderType renderType = RenderTypes.itemTranslucent(material.sprite().atlasLocation());
                    this.quadInstance.setColor(getLayerColorSafe(tints, material));

                    submitNodeCollector.submitCustomGeometry(poseStack, renderType, (inPose, buffer) -> {
                        Vector3fc normalVec = quad.direction().getUnitVec3f();
                        Matrix4f matrix = inPose.pose();
                        Vector3f normal = inPose.transformNormal(normalVec, new Vector3f());
                        int lightEmission = quad.materialInfo().lightEmission();
                        for (int vertex = 0; vertex < 4; vertex++) {
                            Vector3fc position = quad.position(vertex);
                            long packedUv = quad.packedUV(vertex);
                            int vertexColor = quad.bakedColors().color(vertex); // Neo: apply baked color from the quad
                            int light = quadInstance.getLightCoordsWithEmission(vertex, lightEmission);
                            Vector3f pos = matrix.transformPosition(position, new Vector3f());
                            float u = UVPair.unpackU(packedUv);
                            float v = UVPair.unpackV(packedUv);
                            buffer.applyBakedNormals(normal, quad.bakedNormals(), vertex, inPose.normal()); // Neo: apply baked normals from the quad
                            buffer.addVertex(pos)
                                    .setUv(u, v)
                                    .setColor(ARGB.redFloat(vertexColor), ARGB.greenFloat(vertexColor), ARGB.blueFloat(vertexColor), ARGB.alphaFloat(vertexColor) * whiteOverlayProgress)
                                    .setOverlay(overlay)
                                    .setLight(light)
                                    .setNormal(normal.x, normal.y, normal.z);
                        }
                    });
                }
            }
            poseStack.popPose();
        }
    }

    private static int getLayerColorSafe(int[] tintLayers, BakedQuad.MaterialInfo material) {
        return material.isTinted() ? (material.tintIndex() >= 0 && material.tintIndex() < tintLayers.length ? tintLayers[material.tintIndex()] : -1) : -1;
    }
}
