package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.content.particles.BlockParticleDustEmitterOption;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EclipticMeteorState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "ecliptic_meteor.start";
    public static final String ANIMATE_STATE_LOOP = "ecliptic_meteor.loop";
    public static final String ANIMATE_STATE_END = "ecliptic_meteor.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private int maxTicks = 300;
    private List<EntityReference<LivingEntity>> targetingEntitySnapshot = new ArrayList<>();

    public EclipticMeteorState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        Vec3 center = this.owner.getCombatArenaCenter();
        this.owner.teleportTo(center.x, center.y, center.z);
        this.owner.setAnimateState(ANIMATE_STATE_START);
        this.trackingMarker = new TargetMarker(this.owner.level(), center, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_AOE, (float) (this.owner.getArenaRadius() * 2 * Math.sqrt(2)), 200));
        this.owner.level().addFreshEntity(trackingMarker);
        this.owner.setShouldHideBoundingBox(true);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        Vec3 arenaCenter = this.owner.getCombatArenaCenter();
        if(pastTicks < 240){
            if(pastTicks % 2 == 0){
                var alpha = (float) Mth.clamp(pastTicks / 200.0f, 0.1, 1);
                this.owner.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.1f + alpha, alpha);
            }
            if(pastTicks < 170 && pastTicks % 5 == 0){
                ((ServerLevel) this.owner.level()).sendParticles(new BlockParticleDustEmitterOption(AllParticleTypes.DUST_SUCKING.get(), Blocks.STONE.defaultBlockState(), (float) (this.owner.getArenaRadius() * Math.sqrt(2)), 1, 5),
                        arenaCenter.x, arenaCenter.y + 0.5f, arenaCenter.z, 0, 0, 0.0, 0, 0.0);
            }
        }
        if(pastTicks == 15){
            this.owner.setAnimateState(ANIMATE_STATE_LOOP);
        } else if (pastTicks == 160) {
            var remoteMeteor = DelayedAttackMarker.createRemoteMeteor(this.owner.level(), arenaCenter.subtract(0, 1, 0), this.owner, 84, true);
            this.owner.level().addFreshEntity(remoteMeteor);
        } else if (pastTicks == 200) {
            this.owner.setAnimateState(ANIMATE_STATE_END);
            targetingEntitySnapshot.clear();
            this.owner.level().getEntitiesOfClass(LivingEntity.class, this.owner.getCombatArena(), livingEntity -> {
                if(!this.owner.canAttack(livingEntity)) return false;
                var calculatingCenter = arenaCenter.add(0, livingEntity.getY(0.5) - arenaCenter.y, 0);
                return ServerExplosion.getSeenPercent(calculatingCenter, livingEntity) > 0.5;
            }).forEach(livingEntity -> targetingEntitySnapshot.add(EntityReference.of(livingEntity)));
        } else if(currentTick == 0) {
            return false;
        } else {
            if(pastTicks > 210 && pastTicks <= 240){
                if(pastTicks % 5 == 0){
                    this.owner.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 4, 0.5f);
                    ((ServerLevel) this.owner.level()).sendParticles(new BlockParticleDustEmitterOption(AllParticleTypes.DUST_BLOWING.get(), Blocks.STONE.defaultBlockState(), (float) (this.owner.getArenaRadius() * Math.sqrt(2)), 1, 5),
                            arenaCenter.x, arenaCenter.y, arenaCenter.z, 0, 0, 0.0, 0, 0.0);
                }
                if(pastTicks == 239){
                    this.owner.level().playSound(null, BlockPos.containing(arenaCenter), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4, 0.5f);
                }
            }
            if (pastTicks == 240) {
                for (EntityReference<LivingEntity> reference : targetingEntitySnapshot) {
                    var entity = reference.getEntity(this.owner.level(), LivingEntity.class);
                    if (entity != null && this.owner.canAttack(entity) && this.owner.getCombatArena().contains(entity.position())) {
                        entity.hurtServer(((ServerLevel) this.owner.level()), this.owner.damageSources().source(AllDamageTypes.ECLIPTIC_METEOR, this.owner), Float.MAX_VALUE);
                    }
                }
            } else if (pastTicks == 245) {
                BlockPos.betweenClosedStream(this.owner.getCombatArena().setMinY(arenaCenter.y))
                        .filter(pos -> this.owner.level().getBlockState(pos).is(AllBlocks.DEEP_LATENT_BLOCK))
                        .forEach(pos -> this.owner.level().destroyBlock(pos, true, this.owner));
            }
        }
        return true;
    }

    @Override
    public void end() {
        if(trackingMarker != null && trackingMarker.isAlive()) {
            trackingMarker.discard();
        }
        trackingMarker = null;
        this.owner.setAnimateState("idle");
        this.owner.setShouldHideBoundingBox(false);
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", currentTick);
        if(!targetingEntitySnapshot.isEmpty()){
            var list = output.list("Snapshot", EntityReference.<LivingEntity>codec());
            targetingEntitySnapshot.forEach(list::add);
        }
        if(trackingMarker != null){
            output.store("Marker", EntityReference.codec(), EntityReference.of(trackingMarker));
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        currentTick = input.getIntOr("Tick", 0);
        targetingEntitySnapshot.clear();
        input.list("Snapshot", EntityReference.<LivingEntity>codec()).ifPresent(list -> {
            for (EntityReference<LivingEntity> reference : list) {
                targetingEntitySnapshot.add(reference);
            }
        });
        input.read("Marker", EntityReference.<TargetMarker>codec())
                .map(ref -> ref.getEntity(this.owner.level(), TargetMarker.class))
                .ifPresent(targetMarker -> this.trackingMarker = targetMarker);
    }
}
