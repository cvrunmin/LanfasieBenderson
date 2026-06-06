package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.OminousOrbItem;
import io.github.cvrunmin.lanfasie.benderson.content.ProvokingStickItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LanfasieBenderson.MODID);

    public static final DeferredItem<BlockItem> DEEP_LATENT_CALLER = ITEMS.registerSimpleBlockItem(AllBlocks.DEEP_LATENT_CALLER);
    public static final DeferredItem<Item> SWORD_OF_DAWNWAITER = ITEMS.registerItem("sword_of_dawnwaiter", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredItem<Item> SWORD_OF_DAWNWAITER_TAINTED = ITEMS.registerItem("sword_of_dawnwaiter_tainted", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredItem<OminousOrbItem> OMINOUS_ORB = ITEMS.registerItem("ominous_orb", properties -> new OminousOrbItem(properties.useCooldown(5).component(AllDataComponents.ARENA_RADIUS.get(), 24)));
    public static final DeferredItem<ProvokingStickItem> PROVOKING_STICK = ITEMS.registerItem("provoking_stick", properties -> new ProvokingStickItem(properties.useCooldown(2)));

    public static final DeferredItem<SpawnEggItem> UNFORGIVEN_COWARDICE_SPAWN_EGG = ITEMS.registerItem("unforgiven_cowardice_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(AllEntityTypes.UNFORGIVEN_COWARDICE.get())));
    public static final DeferredItem<SpawnEggItem> UNFORGIVEN_INDISCRETION_SPAWN_EGG = ITEMS.registerItem("unforgiven_indiscretion_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(AllEntityTypes.UNFORGIVEN_INDISCRETION.get())));
    public static final DeferredItem<SpawnEggItem> UNFORGIVEN_PERFIDY_SPAWN_EGG = ITEMS.registerItem("unforgiven_perfidy_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(AllEntityTypes.UNFORGIVEN_PERFIDY.get())));
    public static final DeferredItem<SpawnEggItem> UNFORGIVEN_RIDICULE_SPAWN_EGG = ITEMS.registerItem("unforgiven_ridicule_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(AllEntityTypes.UNFORGIVEN_RIDICULE.get())));
    public static final DeferredItem<SpawnEggItem> UNFORGIVEN_SPOILING_SPAWN_EGG = ITEMS.registerItem("unforgiven_spoiling_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(AllEntityTypes.UNFORGIVEN_SPOILING.get())));

    public static void register(IEventBus modBus){
        ITEMS.register(modBus);
    }
}
