package io.github.cvrunmin.lanfasie.benderson.content.anticalabrum;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Vector3fc;

public class AnticalabrumRenderState extends EntityRenderState {
    public Anticalabrum.AnticalabrumType type;
    public int rawLifeTick;
    public float lifeTick;
    public Vector3fc orientation;
}
