package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class UnforgivenSpoilingRenderState extends LivingEntityRenderState {
    public float jumpCompletion;
    public final AnimationState hopAnimationState = new AnimationState();
}
