package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllAttributes;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import io.github.cvrunmin.lanfasie.benderson.utils.VulnerabilityHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Objects;

public class PartialArenaAoeComplexPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_START = "half_arena_aoe_self.start";
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP = "half_arena_aoe_self.loop";
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_END = "half_arena_aoe_self.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private int maxTicks = 390;
    private int maxTicksWithWaiting = maxTicks + 60;
    private int cooldownTick = 0;
    private final float attackDamage;
    private Vec3[] targetPos;

    public PartialArenaAoeComplexPhaseState(Benderson owner, float attackDamage) {
        this.owner = owner;
        this.attackDamage = attackDamage;
    }

    private void computeTargetPos(){
        targetPos = new Vec3[4];
        targetPos[0] = this.owner.getCombatArenaCenterVec3().subtract(0, 0, owner.getArenaRadius() * 0.5f);
        targetPos[1] = this.owner.getCombatArenaCenterVec3().add(owner.getArenaRadius() * 0.5f, 0, 0);
        targetPos[2] = this.owner.getCombatArenaCenterVec3().add(0, 0, owner.getArenaRadius() * 0.5f);
        targetPos[3] = this.owner.getCombatArenaCenterVec3().subtract(owner.getArenaRadius() * 0.5f, 0, 0);
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        computeTargetPos();
        this.owner.getMoveControl().setWantedPosition(targetPos[0].x, targetPos[0].y, targetPos[0].z, 1.0f);
        var marker = new TargetMarker(this.owner.level(), targetPos[0],
                TargetMarker.MarkerArgs.complexRange(TargetMarker.MarkerType.LINEAR_AOE, this.owner.getArenaRadius() * 2, this.owner.getArenaRadius() * 1.5f, 130));
        this.trackingMarker = marker;
        this.currentTick = this.maxTicksWithWaiting;
    }

    @Override
    public boolean tick() {
        if(targetPos == null) computeTargetPos();
        if(maxTicksWithWaiting - currentTick <= 60){
            var distToTgtPos = targetPos[0].distanceTo(this.owner.position());
            if(distToTgtPos < 0.708 || maxTicksWithWaiting - currentTick == 60) {
                this.owner.level().addFreshEntity(this.trackingMarker);
                this.owner.stopInPlace();
                this.owner.teleportTo(targetPos[0].x, targetPos[0].y, targetPos[0].z);
                this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, 1).add(this.owner.position()));
                this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_START);
                this.currentTick = this.maxTicks - 1;
            }else{
                currentTick--;
                this.owner.getMoveControl().setWantedPosition(targetPos[0].x, targetPos[0].y, targetPos[0].z, 1.0f);
            }
            return true;
        }
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        if(pastTicks == 70){
            var delayAttacker = DelayedAttackMarker.createRemoteSweepPartialArena(owner.level(), targetPos[1], owner, owner.getArenaRadius(), owner.getArenaRadius() * 1.5f, new Vector3f(-1, 0, 0), attackDamage, 130);
            owner.level().addFreshEntity(delayAttacker);
        } else if (pastTicks == 140) {
            var delayAttacker = DelayedAttackMarker.createRemoteSweepPartialArena(owner.level(), targetPos[2], owner, owner.getArenaRadius(), owner.getArenaRadius() * 1.5f, new Vector3f(0, 0, -1), attackDamage, 130);
            owner.level().addFreshEntity(delayAttacker);
        } else if(pastTicks == 210) {
            var delayAttacker = DelayedAttackMarker.createRemoteSweepPartialArena(owner.level(), targetPos[3], owner, owner.getArenaRadius(), owner.getArenaRadius() * 1.5f, new Vector3f(1, 0, 0), attackDamage, 130);
            owner.level().addFreshEntity(delayAttacker);
        }
        if(pastTicks == 5){
            this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP);
        } else if (pastTicks == 130) {
            this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_END);
        } else if (pastTicks > 130 && pastTicks <= 140) {
            if(pastTicks % 2 == 0){
                this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), AllSoundEvents.BOSS_SWEEP_SFX.get(), SoundSource.HOSTILE, 1, 1);
                var zOffset = ((pastTicks - 130) / 2f - 1) * this.owner.getArenaRadius() * 1.5f / 5;
                ((ServerLevel) this.owner.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.owner.getX(), this.owner.getY(0.5), this.owner.getZ() + zOffset, 10, this.owner.getArenaRadius(), 0.0, 0, 0.0);
            }
            if(pastTicks == 134){
                if(!this.owner.level().isClientSide()){
                    var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(LivingEntity.class),
                            AABB.ofSize(this.owner.getCombatArenaCenterVec3(), this.owner.getArenaRadius() * 2, 20, this.owner.getArenaRadius() * 2).contract(0, 0, -this.owner.getArenaRadius() * 0.5f)
                                    .intersect(this.owner.getCombatArena()),
                            LivingEntity::canBeSeenByAnyone);
                    for (LivingEntity acceptingTarget : acceptingTargets) {
                        if(acceptingTarget.canBeSeenByAnyone()){
                            float damage = acceptingTarget instanceof Player ? attackDamage : attackDamage * Math.min(1.0f, acceptingTarget.getMaxHealth() / 20f);
                            damage *= (float) this.owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                            acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                                    this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                                    damage);
                            if(acceptingTarget instanceof Player player) {
                                VulnerabilityHelper.addVulnerabilityUp(player);
                            }
                        }
                    }
                }
            }
        } else if(currentTick == 0){
            return false;
        }
        return true;
    }

    @Override
    public void end() {
        this.owner.setAnimateState("idle");
        if(this.trackingMarker != null && this.trackingMarker.isAlive()){
            this.trackingMarker.discard();
        }
        this.trackingMarker = null;
        this.currentTick = 0;
        this.cooldownTick = 600;
        this.owner.setGlobalCooldown(100);
    }

    @Override
    public void inactiveTick() {
        if(this.cooldownTick > 0) this.cooldownTick--;
    }

    @Override
    public boolean canUse() {
        if(this.owner.getAttributeValue(AllAttributes.EXTREME) == 0) return false;
        return cooldownTick <= 0 && this.owner.getTarget() != null && !this.owner.isInGlobalCooldown();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
        output.putInt("Cooldown", this.cooldownTick);
        if(this.trackingMarker != null) {
            if(this.trackingMarker.isAddedToLevel()) {
                output.store("Marker", UUIDUtil.CODEC, this.trackingMarker.getUUID());
            }
            else{
                output.store("MarkerArgs", TargetMarker.MarkerArgs.CODEC, this.trackingMarker.getMarkerArgs());
            }
        }
        if(this.targetPos != null) {
            ValueOutput.TypedOutputList<Vec3> posList = output.list("TargetPos", Vec3.CODEC);
            for (int i = 0; i < targetPos.length; i++) {
                if(targetPos[i] != null) posList.add(targetPos[i]);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
        this.cooldownTick = input.getIntOr("Cooldown", 0);
        var markerUuid = input.read("Marker", UUIDUtil.CODEC);
        input.list("TargetPos", Vec3.CODEC).ifPresent(posList -> {
            Vec3[] vec3s = posList.stream().toArray(Vec3[]::new);
            if(vec3s.length >= 4) targetPos = Arrays.copyOf(vec3s, 4);
        });
        if(markerUuid.isPresent()){
            var entity = this.owner.level().getEntity(markerUuid.get());
            if(entity instanceof TargetMarker marker){
                this.trackingMarker = marker;
            }
        }else{
            input.read("MarkerArgs", TargetMarker.MarkerArgs.CODEC).ifPresent(args -> this.trackingMarker = new TargetMarker(this.owner.level(), this.targetPos[0], args));
        }
    }
}
