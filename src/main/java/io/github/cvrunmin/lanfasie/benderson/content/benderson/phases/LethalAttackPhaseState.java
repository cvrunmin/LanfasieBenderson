package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LethalAttackPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_LETHAL_ATTACK_START = "lethal_attack.start";
    public static final String ANIMATE_STATE_LETHAL_ATTACK_LOOP = "lethal_attack.loop";
    public static final String ANIMATE_STATE_LETHAL_ATTACK_END = "lethal_attack.end";
    private final Benderson owner;
    private LivingEntity currentTarget;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private int maxTicks = (int) (20 * (5.5f + 2.5f));
    private int cooldownTick = 0;

    public LethalAttackPhaseState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        if(this.owner.getTarget() != null){
            currentTarget = this.owner.getTarget();
            var marker = new TargetMarker(this.owner.level(), this.currentTarget, TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.LETHAL_ATTACK, 0, 110));
            this.trackingMarker = marker;
            this.owner.level().addFreshEntity(marker);
            this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, currentTarget.position());
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_START);
            this.currentTick = this.maxTicks;
        }
    }

    @Override
    public boolean tick() {
        if(this.currentTarget == null) return false;
        if(trackingMarker == null || trackingMarker.isRemoved()) return false;
        currentTick--;
        if(maxTicks - currentTick == 5){
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_LOOP);
        } else if (maxTicks - currentTick == 110) {
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_END);
        } else if (maxTicks - currentTick == 112) {
            if(!this.owner.level().isClientSide() && this.currentTarget.canBeSeenByAnyone()){
                var maybeShieldLike = currentTarget.getItemBlockingWith();
                var reductionFactor = 0f;
                if(maybeShieldLike != null){
                    BlocksAttacks blocksAttacks = maybeShieldLike.get(DataComponents.BLOCKS_ATTACKS);
                    if(blocksAttacks != null){
                        blocksAttacks.disable(((ServerLevel) this.owner.level()), currentTarget, 2.0f, maybeShieldLike);
                        reductionFactor = 0.8f;
                        ExperienceOrb.award((ServerLevel) this.owner.level(), currentTarget.position(), 20);
                    }
                }
                currentTarget.hurtServer(((ServerLevel) this.owner.level()),
                        this.owner.damageSources().source(AllDamageTypes.LETHAL_ATTACK, this.owner),
                        18.0f * Math.max(0.05f, 1 - reductionFactor));
                if (currentTarget.isDeadOrDying() && this.trackingMarker != null && this.trackingMarker.isAlive()) {
                    this.trackingMarker.discard();
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
        this.currentTarget = null;
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
