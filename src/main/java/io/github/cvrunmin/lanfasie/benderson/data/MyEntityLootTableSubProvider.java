package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

public class MyEntityLootTableSubProvider extends EntityLootSubProvider {
    public MyEntityLootTableSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return AllEntityTypes.ENTITY_TYPES.getEntries().stream().map(DeferredHolder::value);
    }

    @Override
    public void generate() {
        this.add(AllEntityTypes.UNFORGIVEN_PERFIDY.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).add(LootItem.lootTableItem(AllItems.ECHO_OF_FELIS).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));
        this.add(AllEntityTypes.UNFORGIVEN_RIDICULE.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).add(LootItem.lootTableItem(AllItems.ECHO_OF_METHYL_ORANGE).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));
        this.add(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).add(LootItem.lootTableItem(AllItems.ECHO_OF_ENDER).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));
        this.add(AllEntityTypes.UNFORGIVEN_COWARDICE.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).add(LootItem.lootTableItem(AllItems.ECHO_OF_HYDROUS).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));
        this.add(AllEntityTypes.UNFORGIVEN_SPOILING.get(), LootTable.lootTable().withPool(
                LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer()).add(LootItem.lootTableItem(AllItems.ECHO_OF_VOID_HARE).apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));
    }
}
