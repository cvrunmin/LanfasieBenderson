package io.github.cvrunmin.lanfasie.benderson.content.marker;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

public class TargetMarkerRenderState extends EntityRenderState {
    public TargetMarker.MarkerType markerType;
    public float markerHeight;
    public float overheadOffset;
    public float lifeTimeInTick;
    public int expectedLifeTime;
    public boolean alwaysSee;
    public float range;
    public float range2;
    public Vec3 direction;
    public boolean isFirstPerson;
}
