package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.OminousOrbItem;
import io.github.cvrunmin.lanfasie.benderson.content.ProvokingStickItem;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class AllItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LanfasieBenderson.MODID);

    public static final DeferredItem<Item> AGGRO_UP_ICON = ITEMS.registerSimpleItem("aggro_up_icon");
    public static final DeferredItem<Item> BARDS_LUTE = ITEMS.registerSimpleItem("bards_lute");
    public static final DeferredItem<BlockItem> DEEP_LATENT_CALLER = ITEMS.registerSimpleBlockItem(AllBlocks.DEEP_LATENT_CALLER);
    public static final DeferredItem<BlockItem> END_GUARDIAN_STATUE = ITEMS.registerItem(AllBlocks.END_GUARDIAN_STATUE.getId().getPath(), prop -> new DoubleHighBlockItem(AllBlocks.END_GUARDIAN_STATUE.get(), prop), () -> new Item.Properties().useBlockDescriptionPrefix() );
    public static final DeferredItem<Item> SWORD_OF_DAWNWAITER = ITEMS.registerItem("sword_of_dawnwaiter", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredItem<Item> SWORD_OF_DAWNWAITER_TAINTED = ITEMS.registerItem("sword_of_dawnwaiter_tainted", properties -> new Item(properties.sword(ToolMaterial.DIAMOND, 4.5f, -2.8f)));
    public static final DeferredItem<Item> CLAYMORE_OF_HEI_POWER = ITEMS.registerItem("claymore_of_hei_power", properties -> new Item(properties.sword(ToolMaterial.NETHERITE, 8.5f, -3.2f)));
    public static final DeferredItem<OminousOrbItem> OMINOUS_ORB = ITEMS.registerItem("ominous_orb", OminousOrbItem::new, properties ->
            properties.useCooldown(5)
                    .component(AllDataComponents.ARENA_RADIUS.get(), 24));
    public static final DeferredItem<ProvokingStickItem> PROVOKING_STICK = ITEMS.registerItem("provoking_stick", properties -> new ProvokingStickItem(properties.useCooldown(2)));

    public static final DeferredItem<Item> DAWNWAITER_TOTEM = ITEMS.registerSimpleItem("dawnwaiter_totem");

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
    public static final DeferredItem<BowItem> MUNDANE_PRAISER_BOW = ITEMS.registerItem("mundane_praiser_bow", properties -> new BowItem(properties.enchantable(1)));
    public static final DeferredItem<Item> MUNDANE_PRAISER_CANE = ITEMS.registerItem("mundane_praiser_cane", properties -> new Item(properties));
    public static final DeferredItem<Item> MUNDANE_PRAISER_RAPIER = ITEMS.registerItem("mundane_praiser_rapier", properties -> new Item(properties
            .enchantable(10)
            .durability(1326)
            .component(DataComponents.PIERCING_WEAPON, new PiercingWeapon(true, false, Optional.of(SoundEvents.SPEAR_ATTACK), Optional.of(SoundEvents.SPEAR_HIT)))
            .component(DataComponents.ATTACK_RANGE, new AttackRange(0.0F, 3.5F, 0.0F, 4.5F, 0.0625F, 0.5F))
            .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0F)
            .component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, 15))
            .component(DataComponents.USE_EFFECTS, new UseEffects(true, false, 1.0F))
            .component(DataComponents.WEAPON, new Weapon(1))
    ));
    public static final DeferredItem<Item> MUNDANE_PRAISER_MANA_FOCI = ITEMS.registerItem("mundane_praiser_mana_foci", properties -> new Item(properties));

    public static void register(IEventBus modBus){
        ITEMS.register(modBus);
    }
}
