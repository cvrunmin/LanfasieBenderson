package io.github.cvrunmin.lanfasie.benderson.content.marker;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class AttackTargetMarkerRenderer extends EntityRenderer<AttackTargetMarker, EntityRenderState> {

    private static final int LETHAL_ATTACK_ARROW_1_WIDTH = 103;
    private static final int LETHAL_ATTACK_ARROW_1_HEIGHT = 43;
    private static final int LETHAL_ATTACK_ARROW_2_WIDTH = 71;
    private static final int LETHAL_ATTACK_ARROW_2_HEIGHT = 55;

    public AttackTargetMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
