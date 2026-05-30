package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import io.github.cvrunmin.lanfasie.benderson.utils.VulnerabilityHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CircleAoeSelfPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_CIRCLE_AOE_START = "circle_aoe_self.start";
    public static final String ANIMATE_STATE_CIRCLE_AOE_LOOP = "circle_aoe_self.loop";
    public static final String ANIMATE_STATE_CIRCLE_AOE_END = "circle_aoe_self.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private final int range = 7;
    private int maxTicks = (int) (20 * (5.5f + 2.5f));
    private int cooldownTick = 0;
    private final float attackDamage;

    public CircleAoeSelfPhaseState(Benderson owner) {
        this(owner, 5.0f);
    }

    public CircleAoeSelfPhaseState(Benderson owner, float attackDamage) {
        this.owner = owner;
        this.attackDamage = attackDamage;
    }

    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        var marker = new TargetMarker(this.owner.level(), this.owner,
                TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_AOE, this.range, 110));
        this.trackingMarker = marker;
        this.owner.level().addFreshEntity(marker);
        this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, 1).add(this.owner.position()));
        this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_AOE_START);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        if(trackingMarker == null || trackingMarker.isRemoved()) return false;
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        if(pastTicks == 5){
            this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_AOE_LOOP);
        } else if (pastTicks == 110) {
            this.owner.setAnimateState(ANIMATE_STATE_CIRCLE_AOE_END);
        } else if(pastTicks > 110 && pastTicks <= 120){
            if(pastTicks % 2 == 1){
                double dx = -Mth.sin(this.owner.getYRot() * (float) (Math.PI / 180.0));
                double dz = Mth.cos(this.owner.getYRot() * (float) (Math.PI / 180.0));
                this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), AllSoundEvents.BOSS_SWEEP_SFX.get(), SoundSource.HOSTILE, 1, 1);
                ((ServerLevel) this.owner.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.owner.getX() + dx, this.owner.getY(0.5), this.owner.getZ() + dz, 0, dx, 0.0, dz, 0.0);
            }
            if (pastTicks == 114) {
                if(!this.owner.level().isClientSide()){
                    var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(LivingEntity.class),
                            AABB.ofSize(this.owner.position(), this.range, 10, this.range),
                            livingEntity -> livingEntity.canBeSeenByAnyone() && livingEntity.position().subtract(this.owner.position()).horizontalDistance() <= this.range * 0.5f);
                    for (LivingEntity acceptingTarget : acceptingTargets) {
                        acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                                this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                                acceptingTarget instanceof Player ? attackDamage : attackDamage * 0.2f);
                        if(acceptingTarget instanceof Player player) {
                            VulnerabilityHelper.addVulnerabilityUp(player);
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
