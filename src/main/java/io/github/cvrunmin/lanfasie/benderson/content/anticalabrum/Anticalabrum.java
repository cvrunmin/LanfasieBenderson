package io.github.cvrunmin.lanfasie.benderson.content.anticalabrum;

import com.mojang.serialization.Codec;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityDataSerializers;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.Optional;

public class Anticalabrum extends Entity implements TraceableEntity, IEntityWithComplexSpawn {
    public record AnticalabrumTypeInfo(String suffix, @Nullable Holder<MobEffect> mobEffect, int u, int v){
    }

    public enum AnticalabrumType implements StringRepresentable{
        FELIS_INVISIBILIS("felis_invisibilis", new AnticalabrumTypeInfo("black_cat", AllMobEffects.CURSE_BLACK_CAT, 0, 32), 1),
        NETHER_CERBERUS("nether_cerberus", new AnticalabrumTypeInfo("nether_dog", AllMobEffects.CURSE_NETHER_DOG, 32, 32), 4),
        HYDROUS_DREAMER("hydrous_dreamer", new AnticalabrumTypeInfo("water_planet_dreamer", AllMobEffects.CURSE_HYDRO_DREAMER, 64, 32), 3),
        VOID_HARE("void_hare", new AnticalabrumTypeInfo("void_hare", AllMobEffects.CURSE_VOID_HARE, 96, 32), 0),
        END_GUARDIAN("end_guardian", new AnticalabrumTypeInfo("end_guardian", AllMobEffects.CURSE_END_GUARDIAN, 0, 64), 2),
        EMPTY("empty", new AnticalabrumTypeInfo("empty", null, 0, 96), 0),
        ;
        public static final Codec<AnticalabrumType> CODEC = StringRepresentable.fromEnum(AnticalabrumType::values);
        public static final StreamCodec<ByteBuf, AnticalabrumType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

        private final String typeName;
        private final AnticalabrumTypeInfo info;
        public final int nextTypeIndex;

        AnticalabrumType(String typeName, AnticalabrumTypeInfo info, int nextType){
            this.typeName = typeName;
            this.info = info;
            this.nextTypeIndex = nextType;
        }

        @Override
        public String getSerializedName() {
            return this.typeName;
        }

        public AnticalabrumTypeInfo getInfo() {
            return info;
        }

        public Optional<Holder<MobEffect>> getInfluencingMobEffect(){
            return Optional.ofNullable(info.mobEffect());
        }

        public int getNextTypeIndex() {
            return nextTypeIndex % values().length;
        }
    }

    public static final EntityDataAccessor<AnticalabrumType> SWORD_TYPE = SynchedEntityData.defineId(Anticalabrum.class, AllEntityDataSerializers.SWORD_TYPE.get());
    public static final EntityDataAccessor<Vector3fc> SWORD_ORIENTATION = SynchedEntityData.defineId(Anticalabrum.class, EntityDataSerializers.VECTOR3);

    private int lifeTick;
    private int maxLifeTick;
    private Vector3fc swordOrientation;
    private boolean persistent = false;
    private int range = 24;
    private EntityReference<LivingEntity> owner;
    private RandomSource randomSource;

    public Anticalabrum(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.lifeTick = -15; // animation offset
        randomSource = level.getRandom().fork();
        var rho = Math.toRadians(90 - randomSource.nextDouble() * 30);
        var theta = Math.toRadians(randomSource.nextDouble() * 360);
        this.swordOrientation = new Vector3f((float) (Math.cos(rho) * Math.cos(theta)), (float) Math.sin(rho), (float) (Math.cos(rho) * Math.sin(theta)));
    }

    public Anticalabrum(Level level, Vec3 pos, AnticalabrumType anticalabrumType, int maxLifeTick, int range, @Nullable LivingEntity owner){
        this(AllEntityTypes.ANTICALABRUM.get(), level);
        this.setPos(pos);
        this.setAnticalabrumType(anticalabrumType);
        this.maxLifeTick = Math.max(0, maxLifeTick);
        this.range = range;
        this.owner = EntityReference.of(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(SWORD_TYPE, AnticalabrumType.EMPTY);
        entityData.define(SWORD_ORIENTATION, new Vector3f(0, 1, 0));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if(!persistent) {
                if (lifeTick >= maxLifeTick) {
                    this.discard();
                } else {
                    if(lifeTick == 0){
                        var bs = this.getBlockStateOn();
                        if(!bs.isAir()){
                            this.level().levelEvent(null, 2001, getOnPos(), Block.getId(bs));
                        }
                    }
                    if(lifeTick % 100 == 0){
                        getAnticalabrumType().getInfluencingMobEffect().ifPresent(mobEffect -> {
                            var aabb = AABB.ofSize(Vec3.atLowerCornerOf(this.blockPosition()), this.range * 2, 10, this.range * 2);
                            for (Player player : this.level().getEntitiesOfClass(Player.class, aabb)) {
                                player.addEffect(new MobEffectInstance(mobEffect, 200, 0, true, false, true));
                            }
                        });
                    }
                    handleByCurseType();
                }
            }
        }
        lifeTick++;
    }

    private void handleByCurseType(){
        switch (getAnticalabrumType()) {
            case FELIS_INVISIBILIS -> {
                if (lifeTick == 5 && maxLifeTick > 160) {
                    var safeCol = this.level().getRandom().fork().nextInt(3);
                    for (int i = 0; i < 3; i++) {
                        if(i == safeCol) continue;
                        var attacker = DelayedAttackMarker.createBlackCatSmash(level(), position(), getOwner(), range, i, 20, 110);
                        level().addFreshEntity(attacker);
                    }
                }
            }
            case NETHER_CERBERUS -> {
                if (lifeTick % 40 == 0) {
                    var minX = position().x - range + 1;
                    var minZ = position().z - range + 1;
                    var maxX = minX + range + range - 2;
                    var maxZ = minZ + range + range - 2;
                    var x = randomSource.nextDouble() * (maxX - minX) + minX;
                    var z = randomSource.nextDouble() * (maxZ - minZ) + minZ;
                    var attacker = DelayedAttackMarker.createFireballMeteor(level(), new Vec3(x, position().y, z), getOwner(), 1.5f, 15, 70);
                    level().addFreshEntity(attacker);
                }
            }
            case null, default -> {
            }
        }
    }

    @Override
    public LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, level());
    }

    @Override
    public void restoreFrom(Entity oldEntity) {
        super.restoreFrom(oldEntity);
        if(oldEntity instanceof Anticalabrum anticalabrum){
            this.owner = anticalabrum.owner;
        }
    }

    public Vector3fc getSwordOrientation() {
        return swordOrientation;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read("AntiType", AnticalabrumType.CODEC).ifPresent(this::setAnticalabrumType);
        this.maxLifeTick = input.getInt("MaxLifeTick").filter(v -> v >= 0).orElse(0);
        this.lifeTick = input.getIntOr("LifeTick", 0);
        input.read("SwordOrientation", ExtraCodecs.VECTOR3F).ifPresent(vec -> swordOrientation = vec);
        this.persistent = input.getBooleanOr("Persistent", false);
        input.getInt("Range").ifPresent(v -> this.range = v);
        this.owner = EntityReference.read(input, "Owner");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("AntiType", AnticalabrumType.CODEC, getAnticalabrumType());
        output.putInt("MaxLifeTick", maxLifeTick);
        output.putInt("LifeTick", lifeTick);
        output.store("SwordOrientation", ExtraCodecs.VECTOR3F, this.swordOrientation);
        output.putBoolean("Persistent", this.persistent);
        output.putInt("Range", range);
        EntityReference.store(this.owner, output, "Owner");
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(lifeTick);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.lifeTick = additionalData.readInt();
    }

    public int getLifeTick() {
        return lifeTick;
    }

    public void setAnticalabrumType(AnticalabrumType type){
        this.entityData.set(SWORD_TYPE, type);
    }

    public AnticalabrumType getAnticalabrumType(){
        return this.entityData.get(SWORD_TYPE);
    }
}
