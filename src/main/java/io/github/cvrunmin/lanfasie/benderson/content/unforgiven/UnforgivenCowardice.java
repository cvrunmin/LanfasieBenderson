package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class UnforgivenCowardice extends Monster {
    public UnforgivenCowardice(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().build();
    }
}
