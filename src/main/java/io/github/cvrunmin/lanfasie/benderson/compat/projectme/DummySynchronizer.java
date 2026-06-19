package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.IPhaseState;
import net.minecraft.world.entity.Entity;

public class DummySynchronizer extends AbstractSynchronizer {

    @Override
    public void syncEntity(Benderson entity) {

    }

    @Override
    public void entityRemoval(Benderson entity){

    }

    @Override
    public void entityPhaseStateChanged(Benderson benderson, String phaseId, IPhaseState phaseState) {

    }
}
