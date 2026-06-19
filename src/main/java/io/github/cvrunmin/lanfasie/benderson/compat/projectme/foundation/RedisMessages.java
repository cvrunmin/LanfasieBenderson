package io.github.cvrunmin.lanfasie.benderson.compat.projectme.foundation;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RedisMessages {
    private static final ConcurrentHashMap<Integer, Supplier<? extends RedisMessage>> ID_TO_SUPPLIER_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<? extends RedisMessage>, Integer> CLASS_TO_ID_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger nextId = new AtomicInteger();

    static {
        register(SyncEntityRedisMessage.class, SyncEntityRedisMessage::new);
        register(EntityRemovalRedisMessage.class, EntityRemovalRedisMessage::new);
        register(EntityChangePhaseStateMessage.class, EntityChangePhaseStateMessage::new);
    }

    private static <T extends RedisMessage> void register(Class<T> clazz, Supplier<T> supplier){
        if(CLASS_TO_ID_MAP.containsKey(clazz)){
            LanfasieBenderson.LOGGER.warn("class %s has already registered in RedisMessage!".formatted(clazz.getSimpleName()));
            return;
        }
        var id = nextId.getAndIncrement();
        CLASS_TO_ID_MAP.put(clazz, id);
        ID_TO_SUPPLIER_MAP.put(id, supplier);
    }

    public static int getId(Class<? extends RedisMessage> clazz){
        return Optional.ofNullable(CLASS_TO_ID_MAP.get(clazz)).orElse(-1);
    }

    public static Supplier<? extends RedisMessage> getSupplier(int id){
        return ID_TO_SUPPLIER_MAP.get(id);
    }
}
