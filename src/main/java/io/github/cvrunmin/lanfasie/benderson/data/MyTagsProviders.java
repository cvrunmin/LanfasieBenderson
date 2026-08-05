package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MyTagsProviders {
    public static class MyBiomeTagsProvider extends BiomeTagsProvider {
        public MyBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, LanfasieBenderson.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            tag(AllTags.PREFERRED_PLACE_TO_SPAWN_UNFORGIVEN_MONSTER).add(Biomes.DEEP_DARK);
        }
    }

    public static class MyBlockTagsProvider extends BlockTagsProvider {
        public MyBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, LanfasieBenderson.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AllBlocks.END_GUARDIAN_STATUE.get(), AllBlocks.DEEP_LATENT_BLOCK.get(), AllBlocks.DEEP_LATENT_CALLER.get());
        }
    }

    public static class MyDamageTypeTagsProvider extends DamageTypeTagsProvider {
        public MyDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, LanfasieBenderson.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            this.tag(DamageTypeTags.BYPASSES_ARMOR).add(AllDamageTypes.LETHAL_ATTACK, AllDamageTypes.ECLIPTIC_METEOR);
            this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(AllDamageTypes.LETHAL_ATTACK, AllDamageTypes.ECLIPTIC_METEOR);
            this.tag(DamageTypeTags.NO_KNOCKBACK).add(AllDamageTypes.LETHAL_ATTACK, AllDamageTypes.ECLIPTIC_METEOR, AllDamageTypes.BOSS_NORMAL_ATTACK, AllDamageTypes.BOSS_ABILITY_ATTACK);
            this.tag(DamageTypeTags.BYPASSES_SHIELD).add(AllDamageTypes.BOSS_ABILITY_ATTACK, AllDamageTypes.LETHAL_ATTACK, AllDamageTypes.ECLIPTIC_METEOR);
            this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(AllDamageTypes.BOSS_ABILITY_ATTACK);
        }
    }

    public static class MyEntityTypeTagsProvider extends EntityTypeTagsProvider {
        public MyEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, LanfasieBenderson.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                    .add(AllEntityTypes.BENDERSON.get(),
                    AllEntityTypes.DAWN.get(),
                    AllEntityTypes.MUNDANE_PRAISER_BARD.get(),
                    AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(),
                    AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get());
            tag(EntityTypeTags.CANNOT_BE_PUSHED_ONTO_BOATS)
                    .add(AllEntityTypes.BENDERSON.get(),
                    AllEntityTypes.DAWN.get(),
                    AllEntityTypes.MUNDANE_PRAISER_BARD.get(),
                    AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(),
                    AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get());
            tag(AllTags.IMMUNE_BENDERSON_WIPE_ARENA)
                    .add(AllEntityTypes.BENDERSON.get(),
                    AllEntityTypes.DAWN.get(),
                    AllEntityTypes.MUNDANE_PRAISER_BARD.get(),
                    AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(),
                    AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get());
            tag(AllTags.IGNORE_UNFORGIVEN_INDISCRETION_BROADCAST)
                    .add(AllEntityTypes.BENDERSON.get(),
                    AllEntityTypes.DAWN.get(),
                    AllEntityTypes.MUNDANE_PRAISER_BARD.get(),
                    AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(),
                    AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get()
                    );
            tag(AllTags.SIN_BEARER).add(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(),
                    AllEntityTypes.UNFORGIVEN_COWARDICE.get(),
                    AllEntityTypes.UNFORGIVEN_PERFIDY.get(),
                    AllEntityTypes.UNFORGIVEN_RIDICULE.get(),
                    AllEntityTypes.UNFORGIVEN_SPOILING.get());
            tag(TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "capturing_not_supported")))
                    .add(AllEntityTypes.BENDERSON.get(),
                            AllEntityTypes.DAWN.get(),
                            AllEntityTypes.MUNDANE_PRAISER_BARD.get(),
                            AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(),
                            AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get(),
                            AllEntityTypes.PROJECTED_BENDERSON.get(),
                            AllEntityTypes.LANFASIE.get()
                    );
            tag(AllTags.CAN_GET_PROVOKED)
                    .add(EntityType.SKELETON, EntityType.WITHER_SKELETON, EntityType.BOGGED, EntityType.STRAY, EntityType.PARCHED)
                    .add(EntityType.ZOMBIE, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.CAMEL_HUSK, EntityType.DROWNED, EntityType.HUSK)
                    .add(EntityType.SPIDER, EntityType.CAVE_SPIDER)
                    .add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH)
                    .add(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN)
                    .add(EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.SLIME)
                    .add(EntityType.BREEZE)
                    .add(EntityType.ENDERMAN)
                    .add(EntityType.SHULKER)
                    .add(EntityType.CREEPER)
                    .add(EntityType.HOGLIN)
                    .add(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE)
                    .add(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(),
                            AllEntityTypes.UNFORGIVEN_COWARDICE.get(),
                            AllEntityTypes.UNFORGIVEN_PERFIDY.get(),
                            AllEntityTypes.UNFORGIVEN_RIDICULE.get(),
                            AllEntityTypes.UNFORGIVEN_SPOILING.get());
        }
    }

    public static class MyItemTagsProvider extends ItemTagsProvider {
        public MyItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, LanfasieBenderson.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            tag(ItemTags.SWORDS).add(AllItems.SWORD_OF_DAWNWAITER.get(), AllItems.SHALLOWAY_SWORD.get());
            tag(Tags.Items.MELEE_WEAPON_TOOLS).add(AllItems.SWORD_OF_DAWNWAITER.get(), AllItems.SHALLOWAY_SWORD.get(), AllItems.MUNDANE_PRAISER_RAPIER.get());
            tag(ItemTags.BOW_ENCHANTABLE).add(AllItems.MUNDANE_PRAISER_BOW.get());
            tag(Tags.Items.TOOLS_SHIELD).add(AllItems.SHALLOWAY_SHIELD.get());
            tag(Tags.Items.TOOLS_BOW).add(AllItems.MUNDANE_PRAISER_BOW.get());
        }
    }
}
