package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.nio.ByteBuffer;

public class RedisSynchronizer extends AbstractSynchronizer {
    private final ConfigItemAccessor<String> configItemAccessor;
    private MinecraftServer server;
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, ByteBuf> redisSubscriber;

    public void close(){
        if(redisSubscriber != null){
            redisSubscriber.close();
            redisSubscriber = null;
        }
        if(redisClient != null){
            redisClient.close();
            redisClient = null;
        }
        server = null;
    }

    public void start(MinecraftServer server){
        close();
        var redisUrl = configItemAccessor.getValue();
        if(redisUrl != null){
            this.redisClient = RedisClient.create(redisUrl);
//            this.redisConnection = this.redisClient.connect(RedisByteBufCodec.INSTANCE);
            this.redisSubscriber = this.redisClient.connectPubSub(RedisByteBufCodec.INSTANCE);
            redisSubscriber.sync().subscribe("");
            this.server = server;
        }
    }

    public RedisSynchronizer(ConfigItemAccessor<String> configItemAccessor){
        this.configItemAccessor  = configItemAccessor;
        NeoForge.EVENT_BUS.addListener(this::onServerStart);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStart(ServerStartingEvent event){
        this.start(event.getServer());
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event){
        this.close();
    }

    private static class RedisByteBufCodec implements RedisCodec<String, ByteBuf>{
        public static RedisByteBufCodec INSTANCE = new RedisByteBufCodec();

        @Override
        public String decodeKey(ByteBuffer bytes) {
            return StringCodec.UTF8.decodeKey(bytes);
        }

        @Override
        public ByteBuf decodeValue(ByteBuffer bytes) {
            var byteBuf = Unpooled.buffer(bytes.remaining());
            byteBuf.writeBytes(bytes);
            return byteBuf;
        }

        @Override
        public ByteBuffer encodeKey(String key) {
            return StringCodec.UTF8.encodeKey(key);
        }

        @Override
        public ByteBuffer encodeValue(ByteBuf value) {
            return value.nioBuffer();
        }
    }
}
