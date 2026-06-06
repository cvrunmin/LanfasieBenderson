package io.github.cvrunmin.lanfasie.benderson.index;

import com.mojang.serialization.Codec;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LanfasieBenderson.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARENA_RADIUS = DATA_COMPONENTS.registerComponentType("arena_radius",
            builder -> builder.persistent(Codec.intRange(0, 64)).networkSynchronized(ByteBufCodecs.INT));

    public static void register(IEventBus modBus){
        DATA_COMPONENTS.register(modBus);
    }
}
