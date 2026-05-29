package io.github.cvrunmin.lanfasie.benderson.content.marker;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class DelayedAttackMarkerRenderState extends EntityRenderState {
    public float lifeTick;
    public int maxLifeTick;
    public DelayedAttackMarker.AttackType attackType;
    public float range;
    public float range2;
    public ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}
