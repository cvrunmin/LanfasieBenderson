package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class CircleStackAttackPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_CIRCLE_STACK_ATTACK_START = "circle_stack.start";
    public static final String ANIMATE_STATE_CIRCLE_STACK_ATTACK_LOOP = "circle_stack.loop";
    public static final String ANIMATE_STATE_CIRCLE_STACK_ATTACK_END = "circle_stack.end";
    private final Benderson owner;
    private LivingEntity currentTarget;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private int requiredPlayerToStack = 0;
    private final int range = 5;
    private int maxTicks = (int) (20 * (5.5f + 2.5f));
    private int cooldownTick = 0;
    private float damage;

    public CircleStackAttackPhaseState(Benderson owner, float damage) {
        this.owner = owner;
        this.damage = damage;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        if(this.owner.getTarget() != null){
            this.requiredPlayerToStack = this.owner.getActualEnmityMap().size();
            currentTarget = this.owner.getTarget();
            var marker = new TargetMarker(this.owner.level(), this.currentTarget,
                    TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_STACK, this.range, 110));
            this.trackingMarker = marker;
            this.owner.level().addFreshEntity(marker);
            this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, currentTarget.position());
            this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_STACK_ATTACK_START);
            this.currentTick = this.maxTicks;
        }
    }

    @Override
    public boolean tick() {
        if(this.currentTarget == null) return false;
        if(trackingMarker.isRemoved()) return false;
        currentTick--;
        if(maxTicks - currentTick == 5){
            this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_STACK_ATTACK_LOOP);
        } else if (maxTicks - currentTick == 100) {
            this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_STACK_ATTACK_END);
        } else if (maxTicks - currentTick == 115) {
            if(!this.owner.level().isClientSide()){
                var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(Player.class),
                        AABB.ofSize(this.currentTarget.position(), this.range, 10, this.range),
                        player -> player.isAlive() && player.position().subtract(this.currentTarget.position()).horizontalDistance() <= this.range * 0.5f);
                float damage;
                if(this.requiredPlayerToStack <= 1){
                    damage = 1;
                }else{
                    damage = Mth.lerp(Math.max(0, (requiredPlayerToStack - acceptingTargets.size()) / (float) (requiredPlayerToStack - 1)), 1, 10);
                }
                for (Player acceptingTarget : acceptingTargets) {
                    acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                            this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                            damage);

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
        this.currentTarget = null;
        this.cooldownTick = 300;
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
    public void readAdditionalSaveData(ValueInput input) {
        this.currentTick = input.getIntOr("Tick", 0);
        this.cooldownTick = input.getIntOr("Cooldown", 0);
        var markerUuid = input.read("Marker", UUIDUtil.CODEC);
        if(markerUuid.isPresent()){
            var entity = this.owner.level().getEntity(markerUuid.get());
            if(entity instanceof TargetMarker marker){
                this.trackingMarker = marker;
            }
        }
        this.currentTarget = this.owner.getTarget();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
        output.putInt("Cooldown", this.cooldownTick);
        if(this.trackingMarker != null) {
            output.store("Marker", UUIDUtil.CODEC, this.trackingMarker.getUUID());
        }
    }
}
