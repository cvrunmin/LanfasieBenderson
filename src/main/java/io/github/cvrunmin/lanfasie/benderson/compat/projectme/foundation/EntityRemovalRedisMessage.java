package io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation;

import io.github.cvrunmin.lanfasie.benderson.compat.projectme.RedisSynchronizer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class EntityRemovalRedisMessage extends RedisMessage{
    private UUID uuid;

    public EntityRemovalRedisMessage(){}

    public EntityRemovalRedisMessage(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public void readAdditionalData(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
    }

    @Override
    public void putAdditionalData(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    @Override
    public void handle(RedisSynchronizer synchronizer) {
        if(synchronizer.getCurrentBendersonProjections().containsKey(uuid)){
            var entity = synchronizer.getCurrentBendersonProjections().get(uuid);
            if(!entity.isRemoved()) entity.discard();
        }
    }
}
