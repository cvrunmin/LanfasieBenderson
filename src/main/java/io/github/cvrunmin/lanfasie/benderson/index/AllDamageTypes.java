package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AllDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES = DeferredRegister.create(Registries.DAMAGE_TYPE, LanfasieBenderson.MODID);

    public static final DeferredHolder<DamageType, DamageType> LETHAL_ATTACK = DAMAGE_TYPES.register("lethal_attack", () -> new DamageType("lethal_attack", DamageScaling.NEVER, 0.2f));

    public static void register(IEventBus modBus){
        DAMAGE_TYPES.register(modBus);
    }
}
