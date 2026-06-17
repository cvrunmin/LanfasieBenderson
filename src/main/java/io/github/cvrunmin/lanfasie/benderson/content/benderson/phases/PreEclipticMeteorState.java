package io.github.cvrunmin.lanfasie.benderson.content.benderson.phases;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Objects;

public class PreEclipticMeteorState implements IPhaseState{
    public static final String ANIMATE_STATE_START = "summon_blocking_pile.start";
    public static final String ANIMATE_STATE_LOOP = "summon_blocking_pile.loop";
    public static final String ANIMATE_STATE_END = "summon_blocking_pile.end";
    private final Benderson owner;
    private TargetMarker[] trackingMarkers = new TargetMarker[4];
    private Vec3[] summonPilePoses = new Vec3[4];
    private int currentTick = 0;
    private int maxTicks = 146;
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
            summonPilePoses[i] = this.owner.getCombatArenaCenter().add(offset * (i / 2 == 0 ? -1 : 1), 0, offset * (i == 1 || i == 2 ? 1 : -1));
            trackingMarkers[i] = new TargetMarker(this.owner.level(), summonPilePoses[i], TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.GROUND_PROXIMITY, (float) (this.owner.getArenaRadius() * 0.5 * Math.sqrt(2)), 90));
        }
        this.owner.setAnimateState(ANIMATE_STATE_START);
        this.currentTick = this.maxTicks;
    }

    @Override
    public boolean tick() {
        currentTick--;
        int pastTicks = maxTicks - currentTick;
        if(pastTicks == 5){
            this.owner.setAnimateState(ANIMATE_STATE_LOOP);
        } else if (pastTicks == 10) {
            this.owner.setAnimateState(ANIMATE_STATE_END);
        } else if(pastTicks == 25){
            this.owner.setAnimateState("idle");
            for (int i = 0; i < 4; i++) {
                this.owner.level().addFreshEntity(trackingMarkers[i]);
            }
        } else if(currentTick == 0) {
            return false;
        } else if(pastTicks > 25) {
            var withinPhaseNormalAttackTick = (pastTicks - 26) % 30;
            if(this.owner.getTarget() != null){
                var currentTarget = this.owner.getTarget();
                if(withinPhaseNormalAttackTick == 0){
                    this.owner.swing(InteractionHand.MAIN_HAND);
                }
                if(withinPhaseNormalAttackTick < 10){
                    var distVec = currentTarget.position().subtract(this.owner.position()).horizontal();
                    if(distVec.length() > 3.0f){
                        var newPos = this.owner.position().add(distVec).subtract(distVec.normalize());
                        if(distVec.length() <= 7.0f) {
                            this.owner.getMoveControl().setWantedPosition(newPos.x, newPos.y, newPos.z, 1.0);
                        }else{
                            this.owner.teleportTo(newPos.x, newPos.y, newPos.z);
                        }
                    }
                    this.owner.lookAt(EntityAnchorArgument.Anchor.FEET, currentTarget.position());
                }
                if(withinPhaseNormalAttackTick == 7){
                    this.owner.doHurtTarget(((ServerLevel) this.owner.level()), currentTarget);
                }
            }
            if(pastTicks == 85){
                for (Vec3 pilePose : summonPilePoses) {
                    if(pilePose != null){
                        var remoteMeteor = DelayedAttackMarker.createRemoteMeteor(this.owner.level(), pilePose, this.owner, 10);
                        this.owner.level().addFreshEntity(remoteMeteor);
                    }
                }
            }
            if (pastTicks == 94) {
                if(!this.owner.level().isClientSide()){
                    var acceptingTargets = this.owner.level().getEntities(EntityTypeTest.forClass(LivingEntity.class),
                            this.owner.getCombatArena(),
                            LivingEntity::canBeSeenByAnyone);
                    for (LivingEntity acceptingTarget : acceptingTargets) {
                        float damage = acceptingTarget instanceof Player ? attackDamage : attackDamage * Math.min(1.0f, acceptingTarget.getMaxHealth() / 20f);
                        damage *= (float) this.owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float sumDamage = 0;
                        for (Vec3 pilePose : summonPilePoses) {
                            if(pilePose != null){
                                sumDamage += (float) (damage / acceptingTarget.position().distanceToSqr(pilePose));
                            }
                        }
                        acceptingTarget.hurtServer(((ServerLevel) this.owner.level()),
                                this.owner.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this.owner),
                                sumDamage);
                    }
                }
            } else if(pastTicks == 95) {
                for (Vec3 pilePose : summonPilePoses) {
                    BlockPos pileBottomCenter = BlockPos.containing(pilePose);
                    var random = this.owner.level().getRandom().forkPositional().at(pileBottomCenter);
                    var heights = new int[9];
                    heights[4] = random.nextInt(3, 8);
                    heights[1] = random.nextInt(2, heights[4]);
                    heights[3] = random.nextInt(2, heights[4]);
                    heights[5] = random.nextInt(2, heights[4]);
                    heights[7] = random.nextInt(2, heights[4]);
                    heights[0] = random.nextInt(1, Math.min(heights[1], heights[3]));
                    heights[2] = random.nextInt(1, Math.min(heights[1], heights[5]));
                    heights[6] = random.nextInt(1, Math.min(heights[7], heights[3]));
                    heights[8] = random.nextInt(1, Math.min(heights[7], heights[5]));
                    for (int i = 0; i < 9; i++) {
                        int xOff = (i % 3) - 1;
                        int zOff = (i / 3) - 1;
                        for (int yOff = 0; yOff < heights[i]; yOff++) {
                            BlockPos pos = pileBottomCenter.offset(xOff, yOff, zOff);
                            BlockState blockState = AllBlocks.DEEP_LATENT_BLOCK.get().defaultBlockState();
                            if (this.owner.level().getBlockState(pos).canBeReplaced()) {
                                this.owner.level().setBlockAndUpdate(pos, blockState);
                            }
                        }
                    }

                    this.owner.level().playSound(null, pilePose.x, pilePose.y, pilePose.z, SoundEvents.MACE_SMASH_GROUND, SoundSource.HOSTILE, 1, 0.5f);
                    ((ServerLevel) this.owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                            pilePose.x, pilePose.y, pilePose.z, 0, 0, 0.0, 0, 0.0);
                    ((ServerLevel) this.owner.level()).sendParticles(new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.STONE.defaultBlockState()),
                            pilePose.x, pilePose.y, pilePose.z, 8, 1, 0.0, 1, 0.0);
                }
            }
        }
        return true;
    }

    @Override
    public void end() {
        this.owner.setAnimateState("idle");
        for (TargetMarker trackingMarker : trackingMarkers) {
            if(trackingMarker != null && trackingMarker.isAlive()) trackingMarker.discard();
        }
        Arrays.fill(summonPilePoses, null);
        Arrays.fill(trackingMarkers, null);
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
