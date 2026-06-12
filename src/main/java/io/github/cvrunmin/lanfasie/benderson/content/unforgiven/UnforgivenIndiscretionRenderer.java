package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class UnforgivenIndiscretionRenderer extends MobRenderer<UnforgivenIndiscretion, UnforgivenIndiscretionRenderState, UnforgivenIndiscretionModel> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/unforgiven_indiscretion.png");

    public UnforgivenIndiscretionRenderer(EntityRendererProvider.Context context) {
        super(context, new UnforgivenIndiscretionModel(context.bakeLayer(AllModelLayerLocations.UNFORGIVEN_INDISCRETION)), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(UnforgivenIndiscretionRenderState state) {
        return TEXTURE_LOCATION;
    }

    @Override
    public UnforgivenIndiscretionRenderState createRenderState() {
        return new UnforgivenIndiscretionRenderState();
    }

    @Override
    public void extractRenderState(UnforgivenIndiscretion entity, UnforgivenIndiscretionRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if(entity.isBroadcasting()){
            state.yRot += state.ageInTicks * 360 / 1.5f;
        }
    }
}
