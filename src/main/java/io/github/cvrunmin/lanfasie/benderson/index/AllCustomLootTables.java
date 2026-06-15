package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class AllCustomLootTables {
    public static final ResourceKey<LootTable> BENDERSON_TRIAL_COMPLETED = register("entities/benderson_trial_completed");
    public static final ResourceKey<LootTable> BENDERSON_EXTREME_TRIAL_COMPLETED = register("entities/benderson_extreme_trial_completed");
    public static final ResourceKey<LootTable> BENDERSON_NON_UNRESTRICTED = register("entities/benderson");
    public static final ResourceKey<LootTable> BENDERSON_EXTREME_NON_UNRESTRICTED = register("entities/benderson_extreme");

    private static ResourceKey<LootTable> register(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, path));
    }
}
