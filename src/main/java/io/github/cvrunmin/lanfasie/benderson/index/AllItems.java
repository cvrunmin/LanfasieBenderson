package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.OminousOrbItem;
import io.github.cvrunmin.lanfasie.benderson.content.ProvokingStickItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LanfasieBenderson.MODID);

    public static final DeferredHolder<Item, BlockItem> DEEP_LATENT_CALLER = ITEMS.registerSimpleBlockItem(AllBlocks.DEEP_LATENT_CALLER);
    public static final DeferredHolder<Item, Item> SWORD_OF_DAWNWAITER = ITEMS.registerItem("sword_of_dawnwaiter", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredHolder<Item, Item> SWORD_OF_DAWNWAITER_TAINTED = ITEMS.registerItem("sword_of_dawnwaiter_tainted", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredHolder<Item, OminousOrbItem> OMINOUS_ORB = ITEMS.registerItem("ominous_orb", properties -> new OminousOrbItem(properties.useCooldown(5)));
    public static final DeferredHolder<Item, ProvokingStickItem> PROVOKING_STICK = ITEMS.registerItem("provoking_stick", properties -> new ProvokingStickItem(properties.useCooldown(2)));

    public static void register(IEventBus modBus){
        ITEMS.register(modBus);
    }
}
