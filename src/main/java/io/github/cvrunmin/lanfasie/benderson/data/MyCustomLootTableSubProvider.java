package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.index.AllCustomLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class MyCustomLootTableSubProvider implements LootTableSubProvider {
    public MyCustomLootTableSubProvider(HolderLookup.Provider provider) {

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(AllCustomLootTables.BENDERSON_TRIAL_COMPLETED, LootTable.lootTable());
        output.accept(AllCustomLootTables.BENDERSON_EXTREME_TRIAL_COMPLETED, LootTable.lootTable());
        output.accept(AllCustomLootTables.BENDERSON_NON_UNRESTRICTED, LootTable.lootTable());
        output.accept(AllCustomLootTables.BENDERSON_EXTREME_NON_UNRESTRICTED, LootTable.lootTable());
    }
}
