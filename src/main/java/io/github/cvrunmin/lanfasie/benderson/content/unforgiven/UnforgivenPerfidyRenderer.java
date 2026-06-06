package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class UnforgivenPerfidyRenderer extends MobRenderer<UnforgivenPerfidy, UnforgivenPerfidyRenderState, UnforgivenPerfidyModel> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/unforgiven_perfidy.png");

    public UnforgivenPerfidyRenderer(EntityRendererProvider.Context context) {
        super(context, new UnforgivenPerfidyModel(context.bakeLayer(AllModelLayerLocations.UNFORGIVEN_PERFIDY)), 0.3f);
    }


    @Override
    public Identifier getTextureLocation(UnforgivenPerfidyRenderState state) {
        return TEXTURE_LOCATION;
    }

    @Override
    public UnforgivenPerfidyRenderState createRenderState() {
        return new UnforgivenPerfidyRenderState();
    }
}
