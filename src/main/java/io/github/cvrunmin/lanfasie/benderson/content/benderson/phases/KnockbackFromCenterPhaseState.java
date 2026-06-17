package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.content.particles.BlockParticleDustEmitterOption;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllParticleTypes;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class KnockbackFromCenterPhaseState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "knockback_radial.start";
    public static final String ANIMATE_STATE_LOOP = "knockback_radial.loop";
    public static final String ANIMATE_STATE_END = "knockback_radial.end";
    private final Benderson owner;
    private TargetMarker trackingMarker;
    private int currentTick = 0;
    private final double knockbackMultiplier;
    private int maxTicks = (int) (20 * (6.5f + 2.5f));
    private int cooldownTick = 0;
    private final float attackDamage;

    public KnockbackFromCenterPhaseState(Benderson owner, double knockbackDistanceMultiplier, float attackDamage) {
        this.owner = owner;
        this.knockbackMultiplier = knockbackDistanceMultiplier;
        this.attackDamage = attackDamage;
    }


    @Override
    public void start() {
        if(this.owner.level().isClientSide()) return;
        this.owner.setShouldHideBoundingBox(true);
        this.owner.setAnimateState(ANIMATE_STATE_START);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        if(maxTicks - currentTick > 20 && (trackingMarker == null || trackingMarker.isRemoved())) return false;
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        if(pastTicks == 20){
            this.owner.setAnimateState(ANIMATE_STATE_LOOP);
            trackingMarker = new TargetMarker(this.owner.level(), this.owner.getCombatArenaCenter(), TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.KNOCKBACK_RADIAL, (float) (this.owner.getArenaRadius() * knockbackMultiplier * 2), 110));
            this.owner.level().addFreshEntity(trackingMarker);
        } else if (pastTicks == 126) {
            this.owner.setAnimateState(ANIMATE_STATE_END);
            Vec3 center = this.owner.getCombatArenaCenter();
            this.owner.teleportTo(center.x, center.y, center.z);
        } else if(pastTicks > 130 && pastTicks <= 140){
            if(pastTicks == 131){
                this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 1, 0.5f);
                ((ServerLevel) this.owner.level()).sendParticles(new BlockParticleDustEmitterOption(AllParticleTypes.BLOCK_DUST_BLOWING.get(), Blocks.STONE.defaultBlockState(), (float) (this.owner.getArenaRadius() * knockbackMultiplier), 1, 5),
                        this.owner.getX(), this.owner.getY(), this.owner.getZ(), 0, 0, 0.0, 0, 0.0);
            }
            if(pastTicks % 2 == 1){
                this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.STONE_FALL, SoundSource.HOSTILE, 1, 0.5f);
                ((ServerLevel) this.owner.level()).sendParticles(new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.STONE.defaultBlockState()),
                        this.owner.getX(), this.owner.getY(), this.owner.getZ(), 16, 1, 0.0, 1, 0.0);
            }
            if (pastTicks == 134) {
                if(!this.owner.level().isClientSide()){
                    var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(LivingEntity.class),
                            this.owner.getCombatArena(),
                            LivingEntity::canBeSeenByAnyone);
                    for (LivingEntity acceptingTarget : acceptingTargets) {
                        float damage = acceptingTarget instanceof Player ? attackDamage : attackDamage * Math.min(1.0f, acceptingTarget.getMaxHealth() / 20f);
                        damage *= (float) this.owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        var dir = this.owner.position().subtract(acceptingTarget.position()).horizontal().normalize();
                        acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                                this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                                damage);
                        acceptingTarget.knockback(this.owner.getArenaRadius() * knockbackMultiplier * 0.5, dir.x, dir.z);
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
        this.owner.setShouldHideBoundingBox(false);
        this.owner.setAnimateState("idle");
        if(this.trackingMarker != null && this.trackingMarker.isAlive()){
            this.trackingMarker.remove(Entity.RemovalReason.DISCARDED);
        }
        this.trackingMarker = null;
        this.currentTick = 0;
        cooldownTick = 2400;
        this.owner.setGlobalCooldown(100);
    }

    @Override
    public void inactiveTick() {
        if(this.cooldownTick > 0) this.cooldownTick--;
    }

    @Override
    public boolean canUse() {
        return (this.owner.getBodyState() == Benderson.BodyState.UNFORGIVEN || this.owner.getBodyState() == Benderson.BodyState.UNVEILED) && !this.owner.isInGlobalCooldown();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Tick", this.currentTick);
        output.putInt("Cooldown", this.cooldownTick);
        if(this.trackingMarker != null) {
            output.store("Marker", UUIDUtil.CODEC, this.trackingMarker.getUUID());
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
        }
    }
}
