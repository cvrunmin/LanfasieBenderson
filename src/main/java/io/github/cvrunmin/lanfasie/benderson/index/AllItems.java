package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.OminousOrbItem;
import io.github.cvrunmin.lanfasie.benderson.content.ProvokingStickItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlocksAttacks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LanfasieBenderson.MODID);

    public static final DeferredItem<Item> AGGRO_UP_ICON = ITEMS.registerSimpleItem("aggro_up_icon");
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

    public static final DeferredItem<Item> SHALLOWAY_SWORD = ITEMS.registerItem("shalloway_sword", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.0f)));
    public static final DeferredItem<ShieldItem> SHALLOWAY_SHIELD = ITEMS.registerItem("shalloway_shield", properties -> new ShieldItem(
            properties.equippableUnswappable(EquipmentSlot.OFFHAND)
            .delayedComponent(
                    DataComponents.BLOCKS_ATTACKS,
                    context -> new BlocksAttacks(
                            0.25F,
                            1.0F,
                            List.of(new BlocksAttacks.DamageReduction(135.0F, Optional.empty(), 0.0F, 1.0F)),
                            new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                            Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                            Optional.of(SoundEvents.SHIELD_BLOCK),
                            Optional.of(SoundEvents.SHIELD_BREAK)
                    )
            )
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)));

    public static void register(IEventBus modBus){
        ITEMS.register(modBus);
    }
}
