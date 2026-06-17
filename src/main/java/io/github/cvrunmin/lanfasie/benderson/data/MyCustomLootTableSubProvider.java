package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.foundation.ScaledByExtraHealthNumberProvider;
import io.github.cvrunmin.lanfasie.benderson.index.AllCustomLootTables;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class MyCustomLootTableSubProvider implements LootTableSubProvider {
    public MyCustomLootTableSubProvider(HolderLookup.Provider provider) {

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(AllCustomLootTables.BENDERSON_TRIAL_COMPLETED, LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(AllItems.DAWNWAITER_TOTEM).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                )
        );
        output.accept(AllCustomLootTables.BENDERSON_EXTREME_TRIAL_COMPLETED, LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(AllItems.DAWNWAITER_TOTEM)
                                        .apply(SetItemCountFunction.setCount(ScaledByExtraHealthNumberProvider.forExtreme(ConstantValue.exactly(2)))))
                )
        );
        output.accept(AllCustomLootTables.BENDERSON_NON_UNRESTRICTED, LootTable.lootTable());
        output.accept(AllCustomLootTables.BENDERSON_EXTREME_NON_UNRESTRICTED, LootTable.lootTable());
    }
}
