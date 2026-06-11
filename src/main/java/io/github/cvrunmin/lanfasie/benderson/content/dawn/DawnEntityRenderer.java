package io.github.cvrunmin.lanfasie.benderson.content.dawn;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyItemInHandGeoLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class DawnEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<DawnEntity, R> {
    public DawnEntityRenderer(EntityRendererProvider.Context context){
        super(context, new DefaultedEntityGeoModel<>(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "dawn")));
        withRenderLayer(new MyItemInHandGeoLayer<>(context, this, "rightHand", "leftHand"));
    }

}
