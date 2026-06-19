package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.IPhaseState;
import net.minecraft.world.entity.Entity;

public abstract class AbstractSynchronizer {
    public abstract void syncEntity(Benderson entity);

    public abstract void entityRemoval(Benderson entity);

    public abstract void entityPhaseStateChanged(Benderson benderson, String phaseId, IPhaseState phaseState);
}
