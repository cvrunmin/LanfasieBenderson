package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class PreEclipticMeteorState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "summon_blocking_pile.start";
    public static final String ANIMATE_STATE_LOOP = "summon_blocking_pile.loop";
    public static final String ANIMATE_STATE_END = "summon_blocking_pile.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private TargetMarker[] trackingMarkers = new TargetMarker[4];
    private Vec3[] summonPilePoses = new Vec3[4];
    private int currentTick = 0;
    private int maxTicks = (int) (20 * (4.5f + 2.5f));
    private final float attackDamage;

    public PreEclipticMeteorState(Benderson owner, float attackDamage) {
        this.owner = owner;
        this.attackDamage = attackDamage;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        for (int i = 0; i < 4; i++) {
            int offset = this.owner.getArenaRadius() - 8;
            summonPilePoses[i] = this.owner.getCombatArenaCenter().add(offset * (i / 2 == 0 ? -1 : 1), 0, offset * (i == 2 || i == 3 ? 1 : -1));
            trackingMarkers[i] = new TargetMarker(this.owner.level(), summonPilePoses[i], TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.GROUND_PROXIMITY, (float) (this.owner.getArenaRadius() * 0.5 * Math.sqrt(2)), 90));
            this.owner.level().addFreshEntity(trackingMarkers[i]);
        }
        this.owner.setAnimateState(ANIMATE_STATE_START);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public void end() {

    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public boolean canUse() {
        return this.owner.getTarget() != null && this.owner.getBodyState() == Benderson.BodyState.UNFORGIVEN;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
        if(Arrays.stream(summonPilePoses).anyMatch(Objects::nonNull)){
            var list = output.list("PilePos", Vec3.CODEC);
            for (Vec3 summonPilePose : summonPilePoses) {
                if(summonPilePose != null){
                    list.add(summonPilePose);
                }
            }
        }
        if(Arrays.stream(trackingMarkers).anyMatch(Objects::nonNull)){
            var list = output.list("Markers", EntityReference.<TargetMarker>codec());
            for (var marker : trackingMarkers) {
                if(marker != null){
                    list.add(EntityReference.of(marker));
                }
            }
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
        Arrays.fill(summonPilePoses, null);
        Arrays.fill(trackingMarkers, null);
        input.list("PilePos", Vec3.CODEC).ifPresent(list -> {
            int i = 0;
            for (Vec3 vec3 : list) {
                this.summonPilePoses[i] = vec3;
                if(++i >= summonPilePoses.length) break;
            }
        });
        input.list("Markers", EntityReference.<TargetMarker>codec()).ifPresent(list -> {
            int i = 0;
            Level level = this.owner.level();
            for (var markerRef : list) {
                var marker = markerRef.getEntity(level, TargetMarker.class);
                this.trackingMarkers[i] = marker;
                if(++i >= trackingMarkers.length) break;
            }
        });
    }
}
