package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class AttackTargetMarker extends Entity {
    public enum TargetType implements StringRepresentable {
        ENTITY, POS;

        static final Codec<TargetType> CODEC = StringRepresentable.fromEnum(TargetType::values);
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
        ;

        private String name;

        public static final Codec<MarkerType> CODEC = StringRepresentable.fromEnum(MarkerType::values);

        MarkerType(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record MarkerArgs(MarkerType markerType, float range){
        public static final Codec<MarkerArgs> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(MarkerType.CODEC.fieldOf("MarkerType").forGetter(MarkerArgs::markerType),
                    Codec.FLOAT.fieldOf("Range").forGetter(MarkerArgs::range)
                    ).apply(instance, MarkerArgs::new));
    }

    private TargetType targetType;
    private LivingEntity targetEntity;
    private BlockPos targetPos;
    private MarkerArgs markerArgs;

    public AttackTargetMarker(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public AttackTargetMarker(Level level, LivingEntity target, MarkerArgs markerArgs){
        this(AllEntityTypes.ATTACK_TARGET_MARKER.get(), level);
        this.targetEntity = target;
        this.targetType = TargetType.ENTITY;
        this.markerArgs = markerArgs;
    }

    public AttackTargetMarker(Level level, BlockPos pos, MarkerArgs markerArgs){
        this(AllEntityTypes.ATTACK_TARGET_MARKER.get(), level);
        this.targetPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.targetType = TargetType.POS;
        this.markerArgs = markerArgs;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {

    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.targetType == TargetType.ENTITY && this.targetEntity != null){
            this.setPos(this.targetEntity.position());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.targetType = input.read("TargetType", TargetType.CODEC).orElse(TargetType.POS);
        if(this.targetType == TargetType.ENTITY){
            var uuid = input.read("Entity", UUIDUtil.CODEC);
            if(uuid.isPresent()){
                var entity = level().getEntity(uuid.get());
                if(entity instanceof LivingEntity livingEntity){
                    this.targetEntity = livingEntity;
                }
            }
        }else{
            this.targetPos = input.read("Pos", BlockPos.CODEC).orElse(this.blockPosition());
        }
        this.markerArgs = input.read("MarkerArgs", MarkerArgs.CODEC).orElse(new MarkerArgs(MarkerType.EMPTY, 0f));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("TargetType", TargetType.CODEC, this.targetType);
        if(this.targetType == TargetType.ENTITY){
            output.store("Entity", UUIDUtil.CODEC, this.targetEntity.getUUID());
        }else{
            output.store("Pos", BlockPos.CODEC, this.targetPos);
        }
        output.store("MarkerArgs", MarkerArgs.CODEC, this.markerArgs);
    }
}
