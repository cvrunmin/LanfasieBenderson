package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class UnforgivenSpoilingRenderer extends MobRenderer<UnforgivenSpoiling, UnforgivenSpoilingRenderState, UnforgivenSpoilingModel> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/unforgiven_spoiling.png");

    public UnforgivenSpoilingRenderer(EntityRendererProvider.Context context) {
        super(context, new UnforgivenSpoilingModel(context.bakeLayer(AllModelLayerLocations.UNFORGIVEN_SPOILING)), 0.9f);
    }

    @Override
    public Identifier getTextureLocation(UnforgivenSpoilingRenderState state) {
        return TEXTURE_LOCATION;
    }

    @Override
    public UnforgivenSpoilingRenderState createRenderState() {
        return new UnforgivenSpoilingRenderState();
    }

    @Override
    public void extractRenderState(UnforgivenSpoiling entity, UnforgivenSpoilingRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.jumpCompletion = entity.getJumpCompletion(partialTicks);
        state.hopAnimationState.copyFrom(entity.hopAnimationState);
    }
}
