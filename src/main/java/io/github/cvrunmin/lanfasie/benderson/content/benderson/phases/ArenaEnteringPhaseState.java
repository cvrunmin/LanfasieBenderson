package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ArenaEnteringPhaseState implements IPhaseState{
    private final Benderson owner;
    private int tick;

    public ArenaEnteringPhaseState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public boolean tick() {
        if(tick >= 200) return false;
        tick++;
        if(!this.owner.level().isClientSide()){
            if(tick == 150){
                var arena = owner.getCombatArena();
                var targets = this.owner.level().getEntities(new EntityTypeTest<>() {
                    @Override
                    public @Nullable LivingEntity tryCast(Entity entity) {
                        return !(entity instanceof LivingEntity) || entity instanceof Benderson ? null : (LivingEntity) entity;
                    }

                    @Override
                    public Class<? extends Entity> getBaseClass() {
                        return LivingEntity.class;
                    }
                }, arena, LivingEntity::canBeSeenByAnyone);
                for (LivingEntity target : targets) {
                    float damage;
                    if(target instanceof Player || (target instanceof OwnableEntity && ((OwnableEntity) target).getOwnerReference() != null)){
                        damage = 0;
                    }else{
                        damage = 999999;
                    }
                    target.hurtServer(((ServerLevel) this.owner.level()), this.owner.damageSources().source(AllDamageTypes.LETHAL_ATTACK, this.owner), damage);
                }
            }
            if(tick == 190){
                owner.setBodyState(Benderson.BodyState.DEEP_LATENT);
            }
        }
        return true;
    }

    @Override
    public void end() {

    }

    @Override
    public boolean canUse() {
        return this.owner.getBodyState() == Benderson.BodyState.ENTRANCE;
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        tick = input.getIntOr("Tick", 0);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", tick);
    }
}
