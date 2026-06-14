package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityDataSerializers;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TargetMarker extends Entity implements IEntityWithComplexSpawn, OwnableEntity {

    public enum TargetType implements StringRepresentable {
        ENTITY, POS;

        static final Codec<TargetType> CODEC = StringRepresentable.fromEnum(TargetType::values);
        public static final StreamCodec<ByteBuf, TargetType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        @Override
        public String getSerializedName() {
            return name();
        }
    }

    public enum MarkerType implements StringRepresentable{
        EMPTY("empty"),
        LETHAL_ATTACK("lethal_attack"),
        CIRCLE_STACK("stack_circle"),
        LINEAR_STACK("stack_linear"),
        CIRCLE_AOE("circle_aoe"),
        LINEAR_AOE("linear_aoe"),
        CONE_AOE("cone_aoe"),
        KNOCKBACK_RADIAL("knockback_radial"),
        GROUND_PROXIMITY("ground_proximity"),
        ARENA_HINT("arena_hint"),
        ;

        private String name;

        public static final Codec<MarkerType> CODEC = StringRepresentable.fromEnum(MarkerType::values);
        static final StreamCodec<ByteBuf, MarkerType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

        MarkerType(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record MarkerArgs(MarkerType markerType, float range, float range2, Vec3 direction, int expectedLife){
        public static final Codec<MarkerArgs> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(MarkerType.CODEC.fieldOf("MarkerType").forGetter(MarkerArgs::markerType),
                    Codec.FLOAT.fieldOf("Range").orElse(0f).validate(r -> r >= 0 ? DataResult.success(r) : DataResult.error(() -> "range must be no less than 0")).forGetter(MarkerArgs::range),
                        Codec.FLOAT.fieldOf("Range2").orElse(0f).validate(r -> r >= 0 ? DataResult.success(r) : DataResult.error(() -> "range must be no less than 0")).forGetter(MarkerArgs::range2),
                        Vec3.CODEC.fieldOf("Dir").orElse(new Vec3(0, 0, 1)).forGetter(MarkerArgs::direction),
                        Codec.INT.fieldOf("ExpectedLife").orElse(1).validate(life -> life > 0 ? DataResult.success(life) : DataResult.error(() -> "expectedLife must be larger than 0")).forGetter(MarkerArgs::expectedLife)
                    ).apply(instance, MarkerArgs::new));

        public static final StreamCodec<ByteBuf, MarkerArgs> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public MarkerArgs decode(ByteBuf input) {
                var markerType = MarkerType.STREAM_CODEC.decode(input);
                var range = input.readFloat();
                var range2 = input.readFloat();
                var dir = Vec3.STREAM_CODEC.decode(input);
                var expectedLife = input.readInt();
                return new MarkerArgs(markerType, range, range2, dir, expectedLife);
            }

            @Override
            public void encode(ByteBuf output, MarkerArgs value) {
                MarkerType.STREAM_CODEC.encode(output, value.markerType);
                output.writeFloat(value.range);
                output.writeFloat(value.range2);
                Vec3.STREAM_CODEC.encode(output, value.direction);
                output.writeInt(value.expectedLife);
            }
        };

        public static final MarkerArgs EMPTY = new MarkerArgs(MarkerType.EMPTY, 0, 0, new Vec3(0, 0, 1), 1);

        public static MarkerArgs simple(MarkerType markerType, float range, int expectedLife){
            return new MarkerArgs(markerType, range, range, new Vec3(0, 0, 1), expectedLife);
        }

        public static MarkerArgs complexRange(MarkerType markerType, float range, float range2, int expectedLife){
            return new MarkerArgs(markerType, range, range2, new Vec3(0, 0, 1), expectedLife);
        }

        public static MarkerArgs complexRangeWithDirection(MarkerType markerType, float range, float range2, Vec3 direction, int expectedLife){
            return new MarkerArgs(markerType, range, range2, direction, expectedLife);
        }

        public MarkerArgs{
            if(range < 0){
                throw new IllegalArgumentException("Marker range smaller than 0");
            }
            if(range2 < 0){
                throw new IllegalArgumentException("Marker range smaller than 0");
            }
            if(expectedLife <= 0){
                throw new IllegalArgumentException("Marker expected life must be a positive number");
            }
        }
    }

    public static final EntityDataAccessor<TargetType> TARGET_TYPE_ACCESSOR = SynchedEntityData.defineId(TargetMarker.class, AllEntityDataSerializers.MARKER_TARGET_TYPE.get());
    public static final EntityDataAccessor<MarkerArgs> MARKER_ARGS_ACCESSOR = SynchedEntityData.defineId(TargetMarker.class, AllEntityDataSerializers.MARKER_ARGS.get());
    public static final EntityDataAccessor<Boolean> PERSISTENT_ACCESSOR = SynchedEntityData.defineId(TargetMarker.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> TARGET_ENTITY_SYNCER = SynchedEntityData.defineId(TargetMarker.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    public static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> SOURCE_ENTITY_SYNCER = SynchedEntityData.defineId(TargetMarker.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    private LivingEntity targetEntity;
    private UUID delayedTargetEntityUuid;
    private int delayedTargetEntityGraceTick = 0;
    private int lifeTick = 0;

    private EntityReference<LivingEntity> sourceEntity;
    private int sourceEntityFailCount = 0;

    public TargetMarker(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public TargetMarker(Level level, LivingEntity target, MarkerArgs markerArgs){
        this(AllEntityTypes.TARGET_MARKER.get(), level);
        setTargetType(TargetType.ENTITY);
        this.targetEntity = target;
        this.entityData.set(MARKER_ARGS_ACCESSOR, markerArgs);
        this.entityData.set(TARGET_ENTITY_SYNCER, Optional.of(EntityReference.of(target)));
    }

    public TargetMarker(Level level, Vec3 pos, MarkerArgs markerArgs){
        this(AllEntityTypes.TARGET_MARKER.get(), level);
        setTargetType(TargetType.POS);
        this.setPos(pos);
        this.entityData.set(MARKER_ARGS_ACCESSOR, markerArgs);
    }

    public static TargetMarker byBlockPosBottomCenter(Level level, BlockPos pos, MarkerArgs markerArgs){
        return new TargetMarker(level, pos.getBottomCenter(), markerArgs);
    }

    public static TargetMarker byBlockPosLowerCorner(Level level, BlockPos pos, MarkerArgs markerArgs){
        return new TargetMarker(level, Vec3.atLowerCornerOf(pos), markerArgs);
    }

    public void setSourceEntity(LivingEntity sourceEntity) {
        setSourceEntityRef(EntityReference.of(sourceEntity));
    }

    private void setSourceEntityRef(EntityReference<LivingEntity> ref){
        this.sourceEntity = ref;
        this.entityData.set(SOURCE_ENTITY_SYNCER, Optional.ofNullable(this.sourceEntity));
    }

    public LivingEntity getSourceEntity() {
        return sourceEntity != null ? sourceEntity.getEntity(level(), LivingEntity.class) : null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(TARGET_TYPE_ACCESSOR, TargetType.POS);
        entityData.define(MARKER_ARGS_ACCESSOR, MarkerArgs.EMPTY);
        entityData.define(PERSISTENT_ACCESSOR, false);
        entityData.define(TARGET_ENTITY_SYNCER, Optional.empty());
        entityData.define(SOURCE_ENTITY_SYNCER, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if(level().isClientSide()){
            if(accessor == TARGET_ENTITY_SYNCER){
                Optional<EntityReference<LivingEntity>> maybeEntityRef = entityData.get(TARGET_ENTITY_SYNCER);
                maybeEntityRef.ifPresent(entityRef -> this.targetEntity = EntityReference.getLivingEntity(entityRef, this.level()));
            } else if (accessor == SOURCE_ENTITY_SYNCER) {
                var maybeEntityRef = entityData.get(SOURCE_ENTITY_SYNCER);
                maybeEntityRef.ifPresent(ref -> this.sourceEntity = ref);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTargetType() == TargetType.ENTITY) {
            if (this.targetEntity != null) {
                this.setPos(this.targetEntity.position());
                this.setDeltaMovement(this.targetEntity.getDeltaMovement());
            }else if (this.delayedTargetEntityUuid != null){
                var entity = level().getEntity(delayedTargetEntityUuid);
                if(entity instanceof LivingEntity livingEntity){
                    this.targetEntity = livingEntity;
                    this.entityData.set(TARGET_ENTITY_SYNCER, Optional.of(EntityReference.of(livingEntity)));
                    this.delayedTargetEntityUuid = null;
                }else if(delayedTargetEntityGraceTick++ > 100){
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        if(!isPersistent()){
            lifeTick++;
            if(!this.level().isClientSide() && lifeTick >= getMarkerArgs().expectedLife + 600){
                this.discard();
            }
        }else{
            if (!level().isClientSide() && getMarkerArgs().markerType == MarkerType.ARENA_HINT) {
                if (sourceEntity == null) {
                    sourceEntityFailCount++;
                    if (sourceEntityFailCount >= 5) this.discard();
                } else {
                    var srcEntity = getSourceEntity();
                    if (srcEntity == null || !srcEntity.isAlive()) {
                        sourceEntityFailCount++;
                        if (sourceEntityFailCount >= 5) this.discard();
                    } else {
                        sourceEntityFailCount = 0;
                    }
                }
            }
        }
        if(!isRemoved() && !level().isClientSide()){
            if((this.tickCount % 20 == 1 && this.lifeTick < getMarkerArgs().expectedLife)){
                switch (this.getMarkerArgs().markerType) {
                    case LETHAL_ATTACK -> this.playSound(AllSoundEvents.LETHAL_ATTACK_SFX.get());
                    case CIRCLE_STACK, LINEAR_STACK -> this.playSound(AllSoundEvents.STACK_ATTACK_SFX.get());
                    case null, default -> {
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        return distanceSqr < Mth.square(64.0 * getViewScale());
    }

    private static final EntityDimensions PERSISTENT_DIMENSIONS = EntityDimensions.fixed(0.05f, 0.05f);

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if(isPersistent()){
            return PERSISTENT_DIMENSIONS;
        }
        return super.getDimensions(pose);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setTargetType(input.read("TargetType", TargetType.CODEC).orElse(TargetType.POS));
        if(this.getTargetType() == TargetType.ENTITY){
            var uuid = input.read("Entity", UUIDUtil.CODEC);
            if(uuid.isPresent()){
                var entity = level().getEntity(uuid.get());
                if(entity instanceof LivingEntity livingEntity){
                    this.targetEntity = livingEntity;
                    this.entityData.set(TARGET_ENTITY_SYNCER, Optional.of(EntityReference.of(livingEntity)));
                }else{
                    this.delayedTargetEntityUuid = uuid.get();
                }
            }
        }
        this.entityData.set(MARKER_ARGS_ACCESSOR, input.read("MarkerArgs", MarkerArgs.CODEC).orElse(MarkerArgs.EMPTY));
        input.getInt("LifeTick").ifPresent(v -> lifeTick = v);
        this.setPersistent(input.getBooleanOr("Persistent", false));
        Optional.ofNullable(EntityReference.<LivingEntity>read(input, "Source")).ifPresent(this::setSourceEntityRef);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("TargetType", TargetType.CODEC, this.getTargetType());
        if(this.getTargetType() == TargetType.ENTITY && this.targetEntity != null){
            output.store("Entity", UUIDUtil.CODEC, this.targetEntity.getUUID());
        }
        output.store("MarkerArgs", MarkerArgs.CODEC, this.getMarkerArgs());
        output.putInt("LifeTick", this.lifeTick);
        output.putBoolean("Persistent", this.isPersistent());
        if(sourceEntity != null){
            sourceEntity.store(output, "Source");
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(lifeTick);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        lifeTick = additionalData.readInt();
    }

    public TargetType getTargetType() {
        return this.entityData.get(TARGET_TYPE_ACCESSOR);
    }

    public void setTargetType(TargetType targetType) {
        this.entityData.set(TARGET_TYPE_ACCESSOR, targetType);
    }

    public LivingEntity getTargetEntity() {
        if(targetEntity == null && delayedTargetEntityUuid != null){
            var entity = level().getEntity(delayedTargetEntityUuid);
            if(entity instanceof LivingEntity livingEntity){
                this.targetEntity = livingEntity;
                this.entityData.set(TARGET_ENTITY_SYNCER, Optional.of(EntityReference.of(livingEntity)));
                this.delayedTargetEntityUuid = null;
            }
        }
        return targetEntity;
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return sourceEntity;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.setPos(targetPos.getBottomCenter());
    }

    public void setTargetPos(Vec3 targetPos) {
        this.setPos(targetPos);
    }

    public void setPersistent(boolean persistent) {
        this.entityData.set(PERSISTENT_ACCESSOR, persistent);
    }

    public boolean isPersistent() {
        return this.entityData.get(PERSISTENT_ACCESSOR);
    }

    public int getLifeTick() {
        return lifeTick;
    }

    public MarkerArgs getMarkerArgs() {
        return this.entityData.get(MARKER_ARGS_ACCESSOR);
    }
}
