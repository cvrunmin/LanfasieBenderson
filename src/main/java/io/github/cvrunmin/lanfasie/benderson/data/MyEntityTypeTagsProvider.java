package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class MyEntityTypeTagsProvider extends EntityTypeTagsProvider {
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
