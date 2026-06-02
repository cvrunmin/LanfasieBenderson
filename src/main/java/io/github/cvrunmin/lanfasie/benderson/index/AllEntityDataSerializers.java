package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.utils.CodecUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class AllEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, LanfasieBenderson.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<TargetMarker.TargetType>> MARKER_TARGET_TYPE = ENTITY_DATA_SERIALIZER.register("marker_target_type", () -> EntityDataSerializer.forValueType(TargetMarker.TargetType.STREAM_CODEC));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Anticalabrum.AnticalabrumType>> SWORD_TYPE = ENTITY_DATA_SERIALIZER.register("anticalabrum_type", () -> EntityDataSerializer.forValueType(Anticalabrum.AnticalabrumType.STREAM_CODEC));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<TargetMarker.MarkerArgs>> MARKER_ARGS = ENTITY_DATA_SERIALIZER.register("marker_args", () -> EntityDataSerializer.forValueType(TargetMarker.MarkerArgs.STREAM_CODEC));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<HashMap<UUID, Float>>>> OPTIONAL_UUID_FLOAT_MAP = ENTITY_DATA_SERIALIZER.register("uuid_float_map", () -> EntityDataSerializer.forValueType(CodecUtils.UUID_FLOAT_MAP_STREAM_CODEC.apply(ByteBufCodecs::optional)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<DelayedAttackMarker.AttackType>> ATTACK_TYPE = ENTITY_DATA_SERIALIZER.register("delayed_attack_type", () -> EntityDataSerializer.forValueType(DelayedAttackMarker.AttackType.STREAM_CODEC));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Benderson.BodyState>> BENDERSON_BODY_STATE = ENTITY_DATA_SERIALIZER.register("body_state_benderson", () -> EntityDataSerializer.forValueType(Benderson.BodyState.STREAM_CODEC));

    public static void register(IEventBus modBus){
        ENTITY_DATA_SERIALIZER.register(modBus);
    }
}
