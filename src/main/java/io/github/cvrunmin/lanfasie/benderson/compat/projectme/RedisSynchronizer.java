package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.compat.projectme.content.ProjectedBenderson;
import io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation.*;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.IPhaseState;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.phases.SummonAnticalabrumPhaseState;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RedisSynchronizer extends AbstractSynchronizer {
    private final ConfigItemAccessor<String> configItemAccessor;
    private MinecraftServer server;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, ByteBuf> redisConnection;
    private StatefulRedisPubSubConnection<String, ByteBuf> redisSubscriber;

    private static final long INSTANCE_ID = new Random().nextLong();

    private final Set<Class<? extends RedisMessage>> badMessageType = new HashSet<>();

    private final ConcurrentHashMap<UUID, ProjectedBenderson> currentBendersonProjections = new ConcurrentHashMap<>();

    private final WeakHashMap<Level, Set<EntityReference<Benderson>>> loadedRealEntity = new WeakHashMap<>();

    public void close(){
        if(!currentBendersonProjections.isEmpty()){
            for (Iterator<Map.Entry<UUID, ProjectedBenderson>> iterator = currentBendersonProjections.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<UUID, ProjectedBenderson> entry = iterator.next();
                entry.getValue().discard();
                iterator.remove();
            }
        }
        loadedRealEntity.clear();
        if(redisSubscriber != null){
            redisSubscriber.close();
            redisSubscriber = null;
        }
        if(redisConnection != null){
            redisConnection.close();
            redisConnection = null;
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
            try {
                this.redisConnection = this.redisClient.connect(RedisByteBufCodec.INSTANCE);
                this.redisSubscriber = this.redisClient.connectPubSub(RedisByteBufCodec.INSTANCE);
                this.redisSubscriber.addListener(new RedisPubSubListener<>() {
                    @Override
                    public void message(String channel, ByteBuf message) {
                        if (channel.equals(RedisMessage.CHANNEL)) {
                            var buf = new FriendlyByteBuf(message);
                            var id = buf.readVarInt();
                            var instId = buf.readLong();
                            if (instId == INSTANCE_ID) return;
                            var supplier = RedisMessages.getSupplier(id);
                            if (supplier == null) return;
                            var msg = supplier.get();
                            msg.readAdditionalData(buf);
                            msg.handle(RedisSynchronizer.this);
                        }
                    }

                    @Override
                    public void message(String pattern, String channel, ByteBuf message) {

                    }

                    @Override
                    public void subscribed(String channel, long count) {

                    }

                    @Override
                    public void psubscribed(String pattern, long count) {

                    }

                    @Override
                    public void unsubscribed(String channel, long count) {

                    }

                    @Override
                    public void punsubscribed(String pattern, long count) {

                    }
                });
                redisSubscriber.sync().subscribe(RedisMessage.CHANNEL);
            }catch (RuntimeException e){
                LanfasieBenderson.LOGGER.warn("cannot connect to redis server", e);
                this.redisConnection = null;
                this.redisSubscriber = null;
            }
            this.server = server;
        }
    }

    public RedisSynchronizer(ConfigItemAccessor<String> configItemAccessor){
        this.configItemAccessor = configItemAccessor;
        NeoForge.EVENT_BUS.register(this);
    }

    private void sendMessage(RedisMessage message){
        var id = RedisMessages.getId(message.getClass());
        if(id < 0){
            if (badMessageType.add(message.getClass())) {
                LanfasieBenderson.LOGGER.error("RedisMessage %s does not correctly registered. This type of message will not be sent.".formatted(message.getClass().getSimpleName()));
            }
            return;
        }
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(id);
        buf.writeLong(INSTANCE_ID);
        message.putAdditionalData(buf);
        if(redisConnection == null || !redisConnection.isOpen()) return;
        redisConnection.async().publish(RedisMessage.CHANNEL, buf);
    }

    public boolean hasRealEntityNearby(ServerLevel level, Vec3 pos){
        if(!loadedRealEntity.containsKey(level)) return false;
        var hashset = loadedRealEntity.get(level);
        for (EntityReference<Benderson> reference : hashset) {
            var entity = reference.getEntity(level, Benderson.class);
            if(entity == null) continue;
            var chebDist = chebyshevDist(pos, entity.position());
            if(chebDist <= entity.getArenaRadius() + 1) return true;
        }
        return false;
    }

    private static double chebyshevDist(Vec3 pos, Vec3 pos2) {
        return Math.max(Math.abs(pos.x - pos2.x), Math.max(Math.abs(pos.y - pos2.y), Math.abs(pos.z - pos2.z)));
    }

    @Override
    public void syncEntity(Benderson entity){
        if(entity.level().isClientSide()) return;
        sendMessage(new SyncEntityRedisMessage(entity.getUUID(),
                entity.level().dimension(),
                entity.position(),
                entity.getYHeadRot(),
                entity.getYRot(),
                entity.getXRot(),
                entity.getArenaRadius(),
                entity.getArenaCenter(),
                entity.getBodyState()
                ));
    }

    @Override
    public void entityRemoval(Benderson entity) {
        sendMessage(new EntityRemovalRedisMessage(entity.getUUID()));
    }

    @Override
    public void entityPhaseStateChanged(Benderson benderson, String phaseId, IPhaseState phaseState) {
        var extraData = new CompoundTag();
        if(phaseState instanceof SummonAnticalabrumPhaseState summonAnticalabrumPhaseState){
            extraData.putInt("NextType", summonAnticalabrumPhaseState.getNextType().ordinal());
            extraData.putLong("Seed", summonAnticalabrumPhaseState.getLastSeed());
        }
        var msg = new EntityChangePhaseStateMessage(benderson.getUUID(), phaseId, extraData);
        sendMessage(msg);
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ConcurrentHashMap<UUID, ProjectedBenderson> getCurrentBendersonProjections() {
        return currentBendersonProjections;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStart(ServerStartingEvent event){
        this.start(event.getServer());
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event){
        this.close();
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide()) return;
        if(event.getEntity() instanceof Benderson benderson){
            loadedRealEntity.computeIfAbsent(event.getLevel(), _ -> new HashSet<>()).add(EntityReference.of(benderson));
            for (Iterator<Map.Entry<UUID, ProjectedBenderson>> iterator = currentBendersonProjections.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<UUID, ProjectedBenderson> entry = iterator.next();
                var projected = entry.getValue();
                if(projected.isRemoved()) {
                    iterator.remove();
                    continue;
                }
                if(Objects.equals(projected.level(), event.getLevel())){
                    if(chebyshevDist(projected.getCombatArenaCenterVec3(), benderson.getCombatArenaCenterVec3()) <= benderson.getArenaRadius() + 1){
                        projected.discard();
                        iterator.remove();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event){
        for (Map.Entry<Level, Set<EntityReference<Benderson>>> entry : loadedRealEntity.entrySet()) {
            for (EntityReference<Benderson> reference : entry.getValue()) {
                var entity = reference.getEntity(entry.getKey(), Benderson.class);
                if(entity != null){
                    syncEntity(entity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event){
        if(event.getLevel().isClientSide()) return;
        if(event.getEntity() instanceof Benderson benderson){
            if(loadedRealEntity.containsKey(event.getLevel())){
                var hashset = loadedRealEntity.get(event.getLevel());
                hashset.remove(EntityReference.of(benderson));
                entityRemoval(benderson);
            }
        }
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
