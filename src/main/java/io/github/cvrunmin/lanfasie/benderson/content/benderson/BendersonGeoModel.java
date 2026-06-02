package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.resources.Identifier;

public class BendersonGeoModel extends GeoModel<Benderson> {
    private static final Identifier DEEP_LATENT_MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson");
    private static final Identifier DEEP_LATENT_STANDARD_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson");
    private static final Identifier DEEP_LATENT_ENTRANCE_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson.entrance");
    private static final Identifier DEEP_LATENT_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/benderson.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DEEP_LATENT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DEEP_LATENT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Benderson animatable) {
        if(animatable.getBodyState() == Benderson.BodyState.ENTRANCE) return DEEP_LATENT_ENTRANCE_ANIMATION;
        return DEEP_LATENT_STANDARD_ANIMATION;
    }


}
