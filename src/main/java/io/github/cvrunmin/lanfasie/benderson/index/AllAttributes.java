package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, LanfasieBenderson.MODID);

    public static final DeferredHolder<Attribute, RangedAttribute> ENMITY_MULTIPLIER = ATTRIBUTES.register("enmity_multiplier", () -> new RangedAttribute("attributes.lanfasie_benderson.enmity_multiplier", 1.0, 0.1, 100));
    public static final DeferredHolder<Attribute, BooleanAttribute> EXTREME = ATTRIBUTES.register("extreme", () -> new BooleanAttribute("attributes.lanfasie_benderson.is_extreme", false));

    public static void register(IEventBus modBus){
        ATTRIBUTES.register(modBus);
    }
}
