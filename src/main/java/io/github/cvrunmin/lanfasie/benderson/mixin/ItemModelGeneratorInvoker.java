package io.github.cvrunmin.lanfasie.benderson.mixin;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemModelGenerator.class)
public interface ItemModelGeneratorInvoker {
    @Invoker("bake")
    static QuadCollection invokeBake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, ModelDebugName name, net.minecraft.util.context.ContextMap additionalProperties){
        throw new NotImplementedException();
    }
}
