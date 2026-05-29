package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;

public class BendersonRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<Benderson, R> {
    public BendersonRenderer(EntityRendererProvider.Context ctx){
        super(ctx, AllEntityTypes.BENDERSON.get());
        withRenderLayer(new BendersonWeaponGeoLayer<>(ctx, this));
    }

    @Override
    public void addRenderData(Benderson animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(BendersonDataTickets.ANIMATE_STATE, animatable.getAnimateState());
    }
}
