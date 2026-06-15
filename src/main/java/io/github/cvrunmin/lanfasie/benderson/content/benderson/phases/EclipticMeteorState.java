package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ServerExplosion;
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
        this.trackingMarker = new TargetMarker(this.owner.level(), center, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_AOE, this.owner.getArenaRadius(), 200));
        this.owner.level().addFreshEntity(trackingMarker);
        this.owner.setShouldHideBoundingBox(true);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        if(pastTicks == 5){
            this.owner.setAnimateState(ANIMATE_STATE_LOOP);
        } else if (pastTicks == 200) {
            this.owner.setAnimateState(ANIMATE_STATE_END);
            Vec3 arenaCenter = this.owner.getCombatArenaCenter();
            targetingEntitySnapshot.clear();
            this.owner.level().getEntitiesOfClass(LivingEntity.class, this.owner.getCombatArena(), livingEntity -> {
                if(!this.owner.canAttack(livingEntity)) return false;
                var calculatingCenter = arenaCenter.add(0, livingEntity.getY(0.5) - arenaCenter.y, 0);
                return ServerExplosion.getSeenPercent(calculatingCenter, livingEntity) > 0.5;
            }).forEach(livingEntity -> targetingEntitySnapshot.add(EntityReference.of(livingEntity)));
        } else if(pastTicks == 240){
            for (EntityReference<LivingEntity> reference : targetingEntitySnapshot) {
                var entity = reference.getEntity(this.owner.level(), LivingEntity.class);
                if(entity != null && this.owner.canAttack(entity) && this.owner.getCombatArena().contains(entity.position())){
                    entity.hurtServer(((ServerLevel) this.owner.level()), this.owner.damageSources().source(AllDamageTypes.ECLIPTIC_METEOR), Float.MAX_VALUE);
                }
            }
        } else if (pastTicks == 245) {
            BlockPos.betweenClosedStream(this.owner.getCombatArena().setMinY(this.owner.getCombatArenaCenter().y))
                    .filter(pos -> this.owner.level().getBlockState(pos).is(AllBlocks.DEEP_LATENT_BLOCK))
                    .forEach(pos -> this.owner.level().destroyBlock(pos, true, this.owner));
        } else if(currentTick == 0){
            return false;
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
