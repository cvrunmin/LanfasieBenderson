package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {
    @Accessor("itemTransform")
    ItemTransform getItemTransform();
    @Accessor("localTransform")
    Matrix4f getLocalTransform();

    @Invoker("applyTransform")
    void invokeApplyTransform(PoseStack.Pose localPose);

    @Accessor("specialRenderer")
    SpecialModelRenderer<Object> getSpecialRenderer();

    @Accessor("tintLayers")
    IntList getTintLayers();

    @Accessor("quads")
    List<BakedQuad> getQuads();

    @Accessor("foilType")
    ItemStackRenderState.FoilType getFoilType();

    @Accessor
    @Nullable Object getArgumentForSpecialRendering();
}
