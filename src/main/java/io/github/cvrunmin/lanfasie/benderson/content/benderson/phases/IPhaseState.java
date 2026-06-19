package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.OptionalDouble;

public interface IPhaseState {
    void start();
    boolean tick();
    void end();
    default void inactiveTick(){}
    default boolean canUse(){
        return true;
    }
    default void addAdditionalSaveData(ValueOutput output){}
    default void readAdditionalSaveData(ValueInput input){}
    default OptionalDouble syncSecondForClient(){
        return OptionalDouble.empty();
    }
}
