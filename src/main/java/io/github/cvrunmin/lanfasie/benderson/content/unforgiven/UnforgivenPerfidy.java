package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class UnforgivenPerfidy extends Monster {
    public UnforgivenPerfidy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().build();
    }
}
