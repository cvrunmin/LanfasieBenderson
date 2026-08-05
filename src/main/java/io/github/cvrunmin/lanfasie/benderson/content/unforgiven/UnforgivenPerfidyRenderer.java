package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllModelLayerLocations;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

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

    @Override
    public void extractRenderState(UnforgivenPerfidy entity, UnforgivenPerfidyRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSwelled = entity.isSwelled();
        state.swelling = entity.getSwelling(partialTicks);
        state.isPowered = entity.isSwelling();
    }

    protected void scale(UnforgivenPerfidyRenderState state, PoseStack poseStack) {
        float g = state.swelling;
        float wobble = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float s = (1.0F + g * 0.4F) * wobble;
        float hs = (1.0F + g * 0.1F) / wobble;
        poseStack.scale(s, hs, s);
    }

    protected float getWhiteOverlayProgress(UnforgivenPerfidyRenderState state) {
        float step = state.swelling;
        return (int)(step * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(step, 0.5F, 1.0F);
    }
}
