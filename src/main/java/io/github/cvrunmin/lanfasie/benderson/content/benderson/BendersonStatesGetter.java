package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import net.minecraft.world.phys.Vec3;

public interface BendersonStatesGetter {
    Benderson.BodyState getBodyState();

    boolean isShouldHideBoundingBox();

    String getAnimateState();

    int getArenaRadius();

    Vec3 getCombatArenaCenterVec3();
}
