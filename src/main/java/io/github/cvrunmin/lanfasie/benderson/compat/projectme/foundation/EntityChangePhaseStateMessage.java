package io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation;

import io.github.cvrunmin.lanfasie.benderson.compat.projectme.RedisSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class EntityChangePhaseStateMessage extends RedisMessage{

    private CompoundTag extraData;
    private UUID uuid;
    private String stateId;

    public EntityChangePhaseStateMessage(){}

    public EntityChangePhaseStateMessage(UUID uuid, String stateId, CompoundTag extraData){
        this.uuid = uuid;
        this.stateId = stateId;
        this.extraData = extraData;
    }

    @Override
    public void readAdditionalData(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        stateId = buf.readUtf();
        var hasExtraData = buf.readBoolean();
        if(hasExtraData){
            extraData = buf.readNbt();
        }
    }

    @Override
    public void putAdditionalData(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeUtf(stateId);
        if(extraData != null && !extraData.isEmpty()){
            buf.writeBoolean(true);
            buf.writeNbt(extraData);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void handle(RedisSynchronizer synchronizer) {
        var map = synchronizer.getCurrentBendersonProjections();
        var maybeEntity = map.get(uuid);
        if(maybeEntity != null){
            maybeEntity.setPhaseState(this.stateId, this.extraData);
        }
    }
}
