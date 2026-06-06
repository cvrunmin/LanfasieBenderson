package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class UnforgivenCowardice extends Monster {
    public UnforgivenCowardice(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public UnforgivenCowardice(Level level, double x, double y, double z){
        this(AllEntityTypes.UNFORGIVEN_COWARDICE.get(), level);
        this.setPos(x, y, z);
    }

    public static AttributeSupplier createAttributes(){
        return Monster.createMonsterAttributes().build();
    }
}
