package io.github.cvrunmin.lanfasie.benderson.content.equipment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class ShallowayShieldSpecialRenderer implements NoDataSpecialModelRenderer {
    public static final Transformation DEFAULT_TRANSFORMATION = new Transformation(null, null, new Vector3f(1.0F, -1.0F, -1.0F), null);
    private static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/shalloway_shield.png");

    private final SpriteGetter sprites;
    private final ShallowayShieldModel model;

    public ShallowayShieldSpecialRenderer(SpriteGetter sprites, ShallowayShieldModel model) {
        this.sprites = sprites;
        this.model = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, TEXTURE_LOCATION, lightCoords, overlayCoords, outlineColor, null);

        if (hasFoil) {
            submitNodeCollector.submitModel(
                    this.model, Unit.INSTANCE, poseStack, RenderTypes.entityGlint(), lightCoords, overlayCoords, -1, null, 0, null
            );
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final ShallowayShieldSpecialRenderer.Unbaked INSTANCE = new ShallowayShieldSpecialRenderer.Unbaked();
        public static final MapCodec<ShallowayShieldSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<ShallowayShieldSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        public ShallowayShieldSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new ShallowayShieldSpecialRenderer(context.sprites(), new ShallowayShieldModel(context.entityModelSet().bakeLayer(AllModelLayerLocations.SHALLOWAY_SHIELD)));
        }
    }
}
