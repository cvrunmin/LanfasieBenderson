package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.serialization.Codec;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityDataSerializers;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.utils.VulnerabilityHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DelayedAttackMarker extends Entity implements TraceableEntity, IEntityWithComplexSpawn {

    public static final int CAT_ENTER_TIME = 10;
    public static final int CAT_HALF_DEPTH = 5;
    public static final int CAT_MOVE_SPEED = 2;
    public static final int CAT_LEAVE_TIME = 10;

    public enum AttackType implements StringRepresentable{
        BLACK_CAT_SMASH("black_cat_smash"),
        FIREBALL_METEOR("fireball_meteor"),
        BENDERSON_REMOTE_STACKABLE_METEOR("benderson_remote_stackable_meteor"),
        BENDERSON_REMOTE_ECLIPTIC_METEOR("benderson_remote_ecliptic_meteor");

        public static final Codec<AttackType> CODEC = StringRepresentable.fromEnum(AttackType::values);
        public static final StreamCodec<ByteBuf, AttackType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

        private final String name;

        AttackType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    private static final EntityDataAccessor<AttackType> ATTACK_TYPE_ACCESSOR = SynchedEntityData.defineId(DelayedAttackMarker.class, AllEntityDataSerializers.ATTACK_TYPE.get());
    private static final EntityDataAccessor<Float> RANGE_ACCESSOR = SynchedEntityData.defineId(DelayedAttackMarker.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RANGE2_ACCESSOR = SynchedEntityData.defineId(DelayedAttackMarker.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFETICK_ACCESSOR = SynchedEntityData.defineId(DelayedAttackMarker.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFETICK2_ACCESSOR = SynchedEntityData.defineId(DelayedAttackMarker.class, EntityDataSerializers.INT);

    private EntityReference<LivingEntity> owner;
    private float damage = 1;
    private int lifeTick;
    private List<Tuple<LivingEntity, Double>> snapshotAttackEntities;
    private TargetMarker associatedTargetMarker;

    public DelayedAttackMarker(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static DelayedAttackMarker createBlackCatSmash(Level level, Vec3 arenaCenter, @Nullable LivingEntity owner, float arenaRange, int column, float damage, int castTick){
        var instance = new DelayedAttackMarker(AllEntityTypes.DELAYED_ATTACK_MARKER.get(), level);
        instance.setAttackType(AttackType.BLACK_CAT_SMASH);
        instance.owner = EntityReference.of(owner);
        int columnCentered = column - 1;
        float bandWidth = arenaRange * 2 / 3f;
        float offset = columnCentered * bandWidth;
        Vec3 pos = arenaCenter.add(offset, 0, 0);
        instance.setPos(pos);
        instance.setRange(arenaRange);
        instance.setRange2(bandWidth);
        instance.damage = damage;
        var catMoveTotalTime = (int)((arenaRange + CAT_HALF_DEPTH) * 2 / CAT_MOVE_SPEED);
        instance.setMaxLifeTick(castTick + CAT_ENTER_TIME + catMoveTotalTime + CAT_LEAVE_TIME);
        return instance;
    }

    public static DelayedAttackMarker createFireballMeteor(Level level, Vec3 location, @Nullable LivingEntity owner, float range, float damage, int castTick){
        var instance = new DelayedAttackMarker(AllEntityTypes.DELAYED_ATTACK_MARKER.get(), level);
        instance.setAttackType(AttackType.FIREBALL_METEOR);
        instance.owner = EntityReference.of(owner);
        instance.setPos(location);
        instance.setRange(range);
        instance.damage = damage;
        instance.setMaxLifeTick(castTick + 10);
        return instance;
    }

    public static DelayedAttackMarker createRemoteMeteor(Level level, Vec3 location, @Nullable LivingEntity owner, int lifeTick){
        var instance = new DelayedAttackMarker(AllEntityTypes.DELAYED_ATTACK_MARKER.get(), level);
        instance.setAttackType(AttackType.BENDERSON_REMOTE_STACKABLE_METEOR);
        instance.owner = EntityReference.of(owner);
        instance.setPos(location);
        instance.setMaxLifeTick(lifeTick + 5);
        return instance;
    }

    public static DelayedAttackMarker createRemoteEclipticMeteor(Level level, Vec3 location, @Nullable LivingEntity owner, int lifeTick, int keypointLifeTick, float keypointY){
        var instance = new DelayedAttackMarker(AllEntityTypes.DELAYED_ATTACK_MARKER.get(), level);
        instance.setAttackType(AttackType.BENDERSON_REMOTE_ECLIPTIC_METEOR);
        instance.owner = EntityReference.of(owner);
        instance.setPos(location);
        instance.setRange2(keypointY);
        instance.setMaxLifeTick(lifeTick + 5);
        instance.setKeypointLifeTick(keypointLifeTick + 5);
        return instance;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(ATTACK_TYPE_ACCESSOR, AttackType.FIREBALL_METEOR);
        entityData.define(RANGE_ACCESSOR, 1f);
        entityData.define(RANGE2_ACCESSOR, 1f);
        entityData.define(LIFETICK_ACCESSOR, 0);
        entityData.define(LIFETICK2_ACCESSOR, 0);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTick++;
        final int remainingLife = getMaxLifeTick() - lifeTick;
        if(!level().isClientSide()){
            switch (getAttackType()){
                case BLACK_CAT_SMASH -> {
                    var catMoveTotalTime = (int)((getRange() + CAT_HALF_DEPTH) * 2 / CAT_MOVE_SPEED);
                    if(lifeTick == CAT_ENTER_TIME){
                        associatedTargetMarker = new TargetMarker(level(), position().add(0, 0, -getRange()), TargetMarker.MarkerArgs.complexRange(TargetMarker.MarkerType.LINEAR_AOE, getRange2(), getRange() * 2, getMaxLifeTick() - catMoveTotalTime - CAT_LEAVE_TIME));
                        this.level().addFreshEntity(associatedTargetMarker);
                    }
                    if(remainingLife == catMoveTotalTime + CAT_LEAVE_TIME && getOwner() != null && getOwner().isAlive()){
                        var aabb = AABB.ofSize(position(), getRange2(), 10, getRange() * 2);
                        this.snapshotAttackEntities = level().getEntitiesOfClass(LivingEntity.class, aabb).stream()
                                .filter(LivingEntity::canBeSeenByAnyone)
                                .map(livingEntity -> new Tuple<>(livingEntity, livingEntity.position().z - this.position().z + getRange() + CAT_HALF_DEPTH))
                                .collect(Collectors.toCollection(ArrayList::new));
                    }else if (remainingLife <= catMoveTotalTime + CAT_LEAVE_TIME && remainingLife > CAT_LEAVE_TIME) {
                        var catWalkDistance = (float) (catMoveTotalTime - (remainingLife - CAT_LEAVE_TIME)) * CAT_MOVE_SPEED - 1;
                        if (remainingLife % 2 == 0) {
                            var emitPos = position().add(0, 0, -getRange() + catWalkDistance);
                            this.level().playSound(null, emitPos.x, emitPos.y, emitPos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1, 1);
                            ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, emitPos.x, emitPos.y, emitPos.z, 5, 3, 0, 3, 0);
                        }
                        if (snapshotAttackEntities != null && !snapshotAttackEntities.isEmpty()) {
                            for (Iterator<Tuple<LivingEntity, Double>> iterator = this.snapshotAttackEntities.iterator(); iterator.hasNext(); ) {
                                Tuple<LivingEntity, Double> tuple = iterator.next();
                                if (tuple.getB() <= catWalkDistance) {
                                    var livingEntity = tuple.getA();
                                    if (livingEntity.canBeSeenByAnyone()) {
                                        livingEntity.hurtServer(((ServerLevel) level()),
                                                this.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this, this.getOwner()),
                                                livingEntity instanceof Player ? damage : damage * Math.min(1.0f, livingEntity.getMaxHealth() / 20f));
                                        if(livingEntity instanceof Player player) {
                                            VulnerabilityHelper.addVulnerabilityUp(player);
                                        }
                                    }
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
                case FIREBALL_METEOR -> {
                    if(lifeTick == 1){
                        associatedTargetMarker = new TargetMarker(level(), position(), TargetMarker.MarkerArgs.simple(TargetMarker.MarkerType.CIRCLE_AOE, getRange() * 2, getMaxLifeTick() - 10));
                        this.level().addFreshEntity(associatedTargetMarker);
                    }
                    if(remainingLife == 10){
                        var aabb = AABB.ofSize(position(), getRange() * 2, 10, getRange() * 2);
                        this.snapshotAttackEntities = level().getEntitiesOfClass(LivingEntity.class, aabb).stream()
                                .filter(livingEntity -> livingEntity.canBeSeenByAnyone() && livingEntity.position().distanceTo(position()) <= getRange())
                                .map(livingEntity -> new Tuple<>(livingEntity, 0.0))
                                .toList();
                    } else if (remainingLife == 8) {
                        this.level().playSound(null, position().x, position().y, position().z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1, 1);
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, position().x, position().y, position().z, 0, 0, 0, 0, 0);
                        for (Tuple<LivingEntity, Double> tuple : this.snapshotAttackEntities) {
                            var livingEntity = tuple.getA();
                            if(livingEntity.canBeSeenByAnyone()){
                                livingEntity.hurtServer(((ServerLevel) level()),
                                        this.damageSources().source(AllDamageTypes.BOSS_ABILITY_ATTACK, this, this.getOwner()),
                                        livingEntity instanceof Player ? damage : damage * Math.min(1.0f, livingEntity.getMaxHealth() / 20f));
                                if(livingEntity instanceof Player player) {
                                    VulnerabilityHelper.addVulnerabilityUp(player);
                                }
                            }
                        }
                    }
                }
                case null, default -> {}
            }
            if(lifeTick > getMaxLifeTick()){
                this.discard();
                if(associatedTargetMarker != null && associatedTargetMarker.isAlive()){
                    associatedTargetMarker.discard();
                }
            }
        }else{
            switch (getAttackType()){
                case FIREBALL_METEOR -> {
                    if(remainingLife >= 8 && remainingLife <= 28){
                        var pos = this.position();
                        var offset = 10 * (remainingLife - 8) / 20f;
                        this.level().addParticle(ParticleTypes.SMOKE, pos.x, pos.y + (0.5 + offset) * 3, pos.z, 0, 0, 0);
                    }
                }
                case null, default -> {}
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        return distanceSqr < Mth.square(64.0 * getViewScale());
    }

    public void setAttackType(AttackType attackType) {
        entityData.set(ATTACK_TYPE_ACCESSOR, attackType);
    }

    public AttackType getAttackType() {
        return entityData.get(ATTACK_TYPE_ACCESSOR);
    }

    public void setRange(float range){
        entityData.set(RANGE_ACCESSOR, range);
    }

    public float getRange(){
        return entityData.get(RANGE_ACCESSOR);
    }

    public void setRange2(float range){
        entityData.set(RANGE2_ACCESSOR, range);
    }

    public float getRange2(){
        return entityData.get(RANGE2_ACCESSOR);
    }

    public int getLifeTick(){
        return lifeTick;
    }

    public int getMaxLifeTick(){
        return entityData.get(LIFETICK_ACCESSOR);
    }

    private void setMaxLifeTick(int lifeTick){
        entityData.set(LIFETICK_ACCESSOR, lifeTick);
    }

    public int getKeypointLifeTick(){
        return entityData.get(LIFETICK2_ACCESSOR);
    }

    private void setKeypointLifeTick(int v){
        entityData.set(LIFETICK2_ACCESSOR, v);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setAttackType(input.read("AttackType", AttackType.CODEC).orElse(AttackType.FIREBALL_METEOR));
        this.setRange(input.getFloatOr("Range", 1));
        this.setRange2(input.getFloatOr("Range2", 1));
        this.setMaxLifeTick(input.getIntOr("MaxLifeTick", 20));
        this.setKeypointLifeTick(input.getIntOr("KeypointLifeTick", 0));
        this.lifeTick = input.getIntOr("LifeTick", 0);
        this.damage = input.getFloatOr("Damage", 0);
        this.owner = EntityReference.read(input, "Owner");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("AttackType", AttackType.CODEC, this.getAttackType());
        output.putFloat("Range", getRange());
        output.putFloat("Range2", getRange2());
        output.putInt("LifeTick", lifeTick);
        output.putInt("MaxLifeTick", getMaxLifeTick());
        output.putInt("KeypointLifeTick", getKeypointLifeTick());
        output.putFloat("Damage", damage);
        EntityReference.store(this.owner, output, "Owner");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.lifeTick);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.lifeTick = additionalData.readInt();
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, level());
    }
}
