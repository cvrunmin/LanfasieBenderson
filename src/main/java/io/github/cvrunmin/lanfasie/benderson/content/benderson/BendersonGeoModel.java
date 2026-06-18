package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.resources.Identifier;

public class BendersonGeoModel extends GeoModel<Benderson> {
    private static final Identifier DEEP_LATENT_MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson");
    private static final Identifier DEEP_LATENT_CRYSTAL_MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson_with_crystal");
    private static final Identifier UNFORGIVEN_MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/unforgiven_teamsoul");
    private static final Identifier DEEP_LATENT_STANDARD_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson");
    private static final Identifier DEEP_LATENT_ENTRANCE_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson.entrance");
    private static final Identifier DEEP_LATENT_TU1_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson.change_phase1");
    private static final Identifier DEEP_LATENT_TU2_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson.change_phase2");
    private static final Identifier UNFORGIVEN_ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/benderson.unforgiven");
    private static final Identifier DEEP_LATENT_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/benderson.png");
    private static final Identifier DEEP_LATENT_CRYSTAL_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/benderson_with_crystal.png");
    private static final Identifier UNFORGIVEN_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/benderson_unforgiven.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        Benderson.BodyState bodyState = renderState.getOrDefaultGeckolibData(BendersonDataTickets.BODY_STATE, Benderson.BodyState.DEEP_LATENT);
        return getModelPath(bodyState);
    }

    public static Identifier getModelPath(Benderson.BodyState bodyState) {
        if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN){
            return DEEP_LATENT_CRYSTAL_MODEL;
        }else if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST || bodyState == Benderson.BodyState.UNFORGIVEN){
            return UNFORGIVEN_MODEL;
        }
        return DEEP_LATENT_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        Benderson.BodyState bodyState = renderState.getOrDefaultGeckolibData(BendersonDataTickets.BODY_STATE, Benderson.BodyState.DEEP_LATENT);
        return getTexturePath(bodyState);
    }

    public static Identifier getTexturePath(Benderson.BodyState bodyState) {
        if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN){
            return DEEP_LATENT_CRYSTAL_TEXTURE;
        }else if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST || bodyState == Benderson.BodyState.UNFORGIVEN){
            return UNFORGIVEN_TEXTURE;
        }
        return DEEP_LATENT_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(Benderson animatable) {
        Benderson.BodyState bodyState = animatable.getBodyState();
        return getAnimationPath(bodyState);
    }

    public static Identifier getAnimationPath(Benderson.BodyState bodyState) {
        if(bodyState == Benderson.BodyState.ENTRANCE) return DEEP_LATENT_ENTRANCE_ANIMATION;
        if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN) return DEEP_LATENT_TU1_ANIMATION;
        if(bodyState == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST) return DEEP_LATENT_TU2_ANIMATION;
        if(bodyState == Benderson.BodyState.UNFORGIVEN) return UNFORGIVEN_ANIMATION;
        return DEEP_LATENT_STANDARD_ANIMATION;
    }


}
