package io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation;

import io.github.cvrunmin.lanfasie.benderson.compat.projectme.RedisSynchronizer;
import io.github.cvrunmin.lanfasie.benderson.compat.projectme.content.ProjectedBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.UUID;

public class SyncEntityRedisMessage extends RedisMessage{

    private UUID uuid;
    private ResourceKey<Level> dimension;
    private Vec3 pos;
    private float yHeadRot;
    private float yRot;
    private float xRot;
    private int arenaRadius;
    private BlockPos arenaCenter;
    private Benderson.BodyState bodyState;

    public SyncEntityRedisMessage(){}

    public SyncEntityRedisMessage(UUID uuid, ResourceKey<Level> dimension, Vec3 pos, float yHeadRot, float yRot, float xRot, int arenaRadius, BlockPos arenaCenter, Benderson.BodyState bodyState){
        this.uuid = uuid;
        this.dimension = dimension;
        this.pos = pos;
        this.yHeadRot = yHeadRot;
        this.yRot = yRot;
        this.xRot = xRot;
        this.arenaRadius = arenaRadius;
        this.arenaCenter = arenaCenter;
        this.bodyState = bodyState;
    }

    @Override
    public void readAdditionalData(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        dimension = buf.readResourceKey(Registries.DIMENSION);
        pos = new Vec3(buf.readVector3f());
        yHeadRot = buf.readFloat();
        yRot = buf.readFloat();
        xRot = buf.readFloat();
        arenaRadius = buf.readInt();
        arenaCenter = buf.readBlockPos();
        bodyState = buf.readById(i -> Benderson.BodyState.values()[Mth.clamp(i, 0, Benderson.BodyState.values().length - 1)]);
    }

    @Override
    public void putAdditionalData(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeResourceKey(dimension);
        buf.writeVector3f(pos.toVector3f());
        buf.writeFloat(yHeadRot);
        buf.writeFloat(yRot);
        buf.writeFloat(xRot);
        buf.writeInt(arenaRadius);
        buf.writeBlockPos(arenaCenter);
        buf.writeById(Benderson.BodyState::ordinal, bodyState);
    }

    @Override
    public void handle(RedisSynchronizer synchronizer) {
        var level = synchronizer.getServer().getLevel(dimension);
        if(level == null) return;
        if(synchronizer.hasRealEntityNearby(level, Vec3.atLowerCornerOf(arenaCenter))) return;
        var map = synchronizer.getCurrentBendersonProjections();
        var maybeEntity = map.get(uuid);
        if(maybeEntity == null || maybeEntity.isRemoved() || !maybeEntity.level().dimension().equals(dimension)){
            if(maybeEntity != null){
                maybeEntity.discard();
                map.remove(uuid);
            }
            if(!level.isLoaded(BlockPos.containing(pos))) return;
            var entity = new ProjectedBenderson(level);
            entity.setPos(pos);
            entity.setYHeadRot(yHeadRot);
            entity.setYRot(yRot);
            entity.setXRot(xRot);
            entity.setArenaRadius(arenaRadius);
            entity.setArenaCenter(arenaCenter);
            entity.setBodyState(bodyState);
            level.addFreshEntity(entity);
            map.put(uuid, entity);
        }else{
            maybeEntity.setPos(pos);
            maybeEntity.setYHeadRot(yHeadRot);
            maybeEntity.setYRot(yRot);
            maybeEntity.setXRot(xRot);
            if(maybeEntity.getArenaRadius() != arenaRadius)
                maybeEntity.setArenaRadius(arenaRadius);
            if(!Objects.equals(maybeEntity.getArenaCenter(), arenaCenter))
                maybeEntity.setArenaCenter(arenaCenter);
        }
    }
}
