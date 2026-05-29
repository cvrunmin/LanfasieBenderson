package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.utils.VulnerabilityHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PartialArenaAoePhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_START = "half_arena_aoe_self.start";
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP = "half_arena_aoe_self.loop";
    public static final String ANIMATE_STATE_HALF_ARENA_AOE_SELF_END = "half_arena_aoe_self.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private int maxTicks = (int) (20 * (6.5f + 2.5f));
    private int maxTicksWithWaiting = maxTicks + 60;
    private int cooldownTick = 0;
    private final float attackDamage;
    private Vec3 targetPos;

    public PartialArenaAoePhaseState(Benderson owner, float attackDamage) {
        this.owner = owner;
        this.attackDamage = attackDamage;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        targetPos = this.owner.getCombatArenaCenter().subtract(0, 0, owner.getArenaRadius() * 0.5f);
        this.owner.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0f);
        var marker = new TargetMarker(this.owner.level(), this.owner,
                TargetMarker.MarkerArgs.complexRange(TargetMarker.MarkerType.LINEAR_AOE, this.owner.getArenaRadius() * 2, this.owner.getArenaRadius() * 1.5f, 130));
        this.trackingMarker = marker;
//        this.owner.level().addFreshEntity(marker);
//        this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, 1).add(this.owner.position()));
//        this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_START);
        this.currentTick = this.maxTicksWithWaiting;
    }

    @Override
    public boolean tick() {
        if(maxTicksWithWaiting - currentTick <= 60){
            var distToTgtPos = targetPos.distanceTo(this.owner.position());
            if(distToTgtPos < 0.1 || maxTicksWithWaiting - currentTick == 60) {
                this.owner.level().addFreshEntity(this.trackingMarker);
                this.owner.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, 1).add(this.owner.position()));
                this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_START);
                this.currentTick = this.maxTicks - 1;
            }else{
                currentTick--;
            }
            return true;
        }
        if(trackingMarker.isRemoved()) return false;
        currentTick--;
        if(maxTicks - currentTick == 5){
            this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_LOOP);
        } else if (maxTicks - currentTick == 130) {
            this.owner.setAnimateState(ANIMATE_STATE_HALF_ARENA_AOE_SELF_END);
        } else if (maxTicks - currentTick == 134) {
            if(!this.owner.level().isClientSide()){
                var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(Player.class),
                        AABB.ofSize(this.owner.getCombatArenaCenter(), this.owner.getArenaRadius() * 2, 10, this.owner.getArenaRadius() * 2).contract(0, 0, -this.owner.getArenaRadius() * 0.5f),
                        LivingEntity::isAlive);
                for (Player acceptingTarget : acceptingTargets) {
                    acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                            this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                            attackDamage);
                    VulnerabilityHelper.addVulnerabilityUp(acceptingTarget);
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
            this.trackingMarker.remove(Entity.RemovalReason.DISCARDED);
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
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
        this.cooldownTick = input.getIntOr("Cooldown", 0);
        var markerUuid = input.read("Marker", UUIDUtil.CODEC);
        if(markerUuid.isPresent()){
            var entity = this.owner.level().getEntity(markerUuid.get());
            if(entity instanceof TargetMarker marker){
                this.trackingMarker = marker;
            }
        }else{
            input.read("MarkerArgs", TargetMarker.MarkerArgs.CODEC).ifPresent(args -> this.trackingMarker = new TargetMarker(this.owner.level(), this.owner, args));
        }
    }
}
