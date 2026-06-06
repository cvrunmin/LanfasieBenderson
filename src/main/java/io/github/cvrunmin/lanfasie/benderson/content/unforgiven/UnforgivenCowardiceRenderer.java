package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class UnforgivenCowardiceRenderer extends MobRenderer<UnforgivenCowardice, UnforgivenCowardiceRenderState, UnforgivenCowardiceModel> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/unforgiven_cowardice.png");

    public UnforgivenCowardiceRenderer(EntityRendererProvider.Context context) {
        super(context, new UnforgivenCowardiceModel(context.bakeLayer(AllModelLayerLocations.UNFORGIVEN_COWARDICE)), 0.9f);
    }

    @Override
    public Identifier getTextureLocation(UnforgivenCowardiceRenderState state) {
        return TEXTURE_LOCATION;
    }

    @Override
    public UnforgivenCowardiceRenderState createRenderState() {
        return new UnforgivenCowardiceRenderState();
    }
}
