package io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyItemInHandGeoLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class MundanePraiserBardRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<MundanePraiserBard, R> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/mundane_praiser");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "entity/mundane_praiser_bard");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/mundane_praiser_bard.png");

    public MundanePraiserBardRenderer(EntityRendererProvider.Context context){
        super(context, new GeoModel<>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return MODEL;
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return TEXTURE;
            }

            @Override
            public Identifier getAnimationResource(MundanePraiserBard animatable) {
                return ANIMATION;
            }
        });
        withRenderLayer(new MyItemInHandGeoLayer<>(context, this, "rightHand", "leftHand"));
    }
}
