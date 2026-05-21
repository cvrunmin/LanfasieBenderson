package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.marker.AttackTargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.BlocksAttacks;

public class LethalAttackPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_LETHAL_ATTACK_START = "lethal_attack.start";
    public static final String ANIMATE_STATE_LETHAL_ATTACK_LOOP = "lethal_attack.loop";
    public static final String ANIMATE_STATE_LETHAL_ATTACK_END = "lethal_attack.end";
    private final Benderson owner;
    private LivingEntity currentTarget;
    private AttackTargetMarker trackingMarker;
    private int currentTick = 0;
    private int maxTicks = (int) (20 * (5.5f + 2.5f));

    public LethalAttackPhaseState(Benderson owner) {
        this.owner = owner;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        if(this.owner.getTarget() != null){
            currentTarget = this.owner.getTarget();
            var marker = new AttackTargetMarker(this.owner.level(), this.currentTarget);
            this.trackingMarker = marker;
            this.owner.level().addFreshEntity(marker);
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_START);
            this.currentTick = this.maxTicks;
        }
    }

    @Override
    public boolean tick() {
        if(this.currentTarget == null) return false;
        if(trackingMarker.isRemoved()) return false;
        currentTick--;
        if(maxTicks - currentTick == 5){
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_LOOP);
        } else if (maxTicks - currentTick == 110) {
            this.owner.setAnimateState(ANIMATE_STATE_LETHAL_ATTACK_END);
        } else if (maxTicks - currentTick == 112) {
            if(!this.owner.level().isClientSide()){
                var maybeShieldLike = currentTarget.getItemBlockingWith();
                var reductionFactor = 0f;
                if(maybeShieldLike != null){
                    BlocksAttacks blocksAttacks = maybeShieldLike.get(DataComponents.BLOCKS_ATTACKS);
                    if(blocksAttacks != null){
                        blocksAttacks.disable(((ServerLevel) this.owner.level()), currentTarget, 2.0f, maybeShieldLike);
                        reductionFactor = 0.8f;
                    }
                }
                currentTarget.hurtServer(((ServerLevel) this.owner.level()),
                        this.owner.damageSources().source(AllDamageTypes.LETHAL_ATTACK.getKey(), this.owner),
                        18.0f * Math.max(0.05f, 1 - reductionFactor));
            }
        } else if(currentTick == 0){
            return false;
        }
        return true;
    }

    @Override
    public void end() {
        if(this.trackingMarker.isAlive()){
            this.trackingMarker.remove(Entity.RemovalReason.DISCARDED);
        }
        this.trackingMarker = null;
        this.currentTick = 0;
        this.currentTarget = null;
    }
}
