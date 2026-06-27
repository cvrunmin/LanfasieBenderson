package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class UnforgivenRidiculeRenderer extends MobRenderer<UnforgivenRidicule, UnforgivenRidiculeRenderState, UnforgivenRidiculeModel> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/unforgiven_ridicule.png");

    public UnforgivenRidiculeRenderer(EntityRendererProvider.Context context) {
        super(context, new UnforgivenRidiculeModel(context.bakeLayer(AllModelLayerLocations.UNFORGIVEN_RIDICULE)), 0.5f);
    }

    @Override
    public Identifier getTextureLocation(UnforgivenRidiculeRenderState state) {
        return TEXTURE_LOCATION;
    }

    @Override
    public UnforgivenRidiculeRenderState createRenderState() {
        return new UnforgivenRidiculeRenderState();
    }

    @Override
    public void extractRenderState(UnforgivenRidicule entity, UnforgivenRidiculeRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isCharging = entity.isCharged();
    }
}
