package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;

public class BendersonRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<Benderson, R> {
    public BendersonRenderer(EntityRendererProvider.Context ctx, EntityType<Benderson> entityType){
        super(ctx, entityType);
    }
}
