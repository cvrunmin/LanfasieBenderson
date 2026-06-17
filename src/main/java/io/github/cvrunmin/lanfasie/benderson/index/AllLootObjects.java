package io.github.cvrunmin.lanfasie.benderson.index;

import com.mojang.serialization.MapCodec;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.ScaledByExtraHealthNumberProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllLootObjects {
    private static final DeferredRegister<MapCodec<? extends NumberProvider>> LOOT_NUMBER_PROVIDER_TYPES = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, LanfasieBenderson.MODID);

    public static final Supplier<MapCodec<? extends NumberProvider>> SCALED_BY_EXTRA_HEALTH = LOOT_NUMBER_PROVIDER_TYPES.register("scaled_by_extra_health", () -> ScaledByExtraHealthNumberProvider.MAP_CODEC);

    public static void register(IEventBus modBus){
        LOOT_NUMBER_PROVIDER_TYPES.register(modBus);
    }
}
