package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.mixin.ItemModelGeneratorInvoker;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import org.jspecify.annotations.Nullable;

public class ItemBlockModelLoader implements UnbakedModelLoader<ItemBlockModelLoader.UnbakedModel> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "item_block");
    public static final ItemBlockModelLoader INSTANCE = new ItemBlockModelLoader();

    private ItemBlockModelLoader(){}

    @Override
    public UnbakedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        StandardModelParameters params = StandardModelParameters.parse(jsonObject, deserializationContext);
        return new UnbakedModel(params);
    }

    public static class UnbakedModel extends AbstractUnbakedModel{

        protected UnbakedModel(StandardModelParameters parameters) {
            super(parameters);
        }

        @Override
        public @Nullable UnbakedGeometry geometry() {
            return (ExtendedUnbakedGeometry) UnbakedModel::bake;
        }

        private static QuadCollection bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, ModelDebugName name, net.minecraft.util.context.ContextMap additionalProperties){
            var transform = additionalProperties.getOptional(net.neoforged.neoforge.client.model.NeoForgeModelProperties.TRANSFORM);
            if (transform != null) {
                modelState = net.neoforged.neoforge.client.model.UnbakedElementsHelper.composeRootTransformIntoModelState(modelState, transform);
            }
            return ItemModelGeneratorInvoker.invokeBake(textureSlots, modelBaker, modelState, name, additionalProperties);
        }
    }

    public static class Builder extends CustomLoaderBuilder{

        public Builder() {
            super(ID, false);
        }

        @Override
        protected CustomLoaderBuilder copyInternal() {
            var copy = new Builder();
            return copy;
        }
    }
}
