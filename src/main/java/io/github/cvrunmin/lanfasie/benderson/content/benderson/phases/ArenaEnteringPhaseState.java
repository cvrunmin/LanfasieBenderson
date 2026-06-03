package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllTags;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ArenaEnteringPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "arena_entering";
    private final Benderson owner;
    private int tick;

    public ArenaEnteringPhaseState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        tick = 0;
        owner.setAnimateState(ANIMATE_STATE_START);
    }

    @Override
    public boolean tick() {
        if(tick >= 200) return false;
        tick++;
        if(!this.owner.level().isClientSide()){
            if(tick == 108){
                var arena = owner.getCombatArena();
                var targets = this.owner.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), arena, livingEntity -> {
                    if(livingEntity instanceof Benderson) return false;
                    return livingEntity.canBeSeenByAnyone();
                });
                for (LivingEntity target : targets) {
                    float damage;
                    if(target instanceof Player
                            || (target instanceof OwnableEntity && ((OwnableEntity) target).getOwnerReference() != null)
                            || target.is(AllTags.IMMUNE_BENDERSON_WIPE_ARENA)){
                        damage = 0;
                    }else{
                        damage = 999999;
                    }
                    target.hurtServer(((ServerLevel) this.owner.level()), this.owner.damageSources().source(AllDamageTypes.LETHAL_ATTACK, this.owner), damage);
                }
            }
            if(tick >= 105 && tick <= 120){
                var zOffset = ((tick - 105) / 15f * 2 - 1) * owner.getArenaRadius();
                ((ServerLevel) owner.level()).sendParticles(ParticleTypes.CRIT, owner.getX() + zOffset, owner.getY() + 0.5, owner.getZ(), owner.getArenaRadius() * 2, 0, 0, owner.getArenaRadius() * 0.75, 0);
                owner.level().playSound(null, owner.getX() + zOffset, owner.getY() + 0.5, owner.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1f, 0.75f);
            }
            if(tick == 155){
                owner.setBodyState(Benderson.BodyState.DEEP_LATENT);
                owner.setAnimateState("idle");
                ((ServerLevel) owner.level()).sendParticles(new DustParticleOptions(0xff610d46, 10), owner.getX(), owner.getY() + owner.getBbHeight() * 0.5, owner.getZ(), 25, owner.getBbWidth(), owner.getBbHeight(), owner.getBbWidth(), 0);
                owner.level().playSound(null, owner, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1f, 0.75f);
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
