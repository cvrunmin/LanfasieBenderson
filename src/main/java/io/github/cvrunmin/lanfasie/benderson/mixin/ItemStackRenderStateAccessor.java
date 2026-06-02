package io.github.cvrunmin.lanfasie.benderson.mixin;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {
    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] getLayers();
    @Accessor("activeLayerCount")
    int getActiveLayerCount();

    @Accessor("displayContext")
    ItemDisplayContext getDisplayContext();
}
