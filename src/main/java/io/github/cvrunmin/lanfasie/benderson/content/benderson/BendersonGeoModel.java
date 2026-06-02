package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.resources.Identifier;

public class BendersonGeoModel extends GeoModel<Benderson> {
    private static final Identifier DEEP_LATENT_MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "benderson");
    private static final Identifier DEEP_LATENT_STANDARD_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "benderson");
    private static final Identifier DEEP_LATENT_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "benderson.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DEEP_LATENT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DEEP_LATENT_STANDARD_ANIMATION;
    }

    @Override
    public Identifier getAnimationResource(Benderson animatable) {
        return DEEP_LATENT_TEXTURE;
    }
}
