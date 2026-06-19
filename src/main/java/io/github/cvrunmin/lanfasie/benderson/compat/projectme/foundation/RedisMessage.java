package io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation;

import io.github.cvrunmin.lanfasie.benderson.compat.projectme.RedisSynchronizer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

public abstract class RedisMessage {
    public static final String CHANNEL = "lanfasie_benderson_projection";

    public abstract void readAdditionalData(FriendlyByteBuf buf);

    public abstract void putAdditionalData(FriendlyByteBuf buf);

    public void handle(RedisSynchronizer synchronizer){

    }
}
