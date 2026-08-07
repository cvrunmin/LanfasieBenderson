package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class AllBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_UNFORGIVEN_MONSTERS = create("add_unforgiven_monsters");
    public static final ResourceKey<BiomeModifier> ADD_UNFORGIVEN_MONSTERS_COST = create("add_unforgiven_monsters_cost");

    private static ResourceKey<BiomeModifier> create(String path){
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, path));
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context){
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(ADD_UNFORGIVEN_MONSTERS,
                new BiomeModifiers.AddSpawnsBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        WeightedList.<MobSpawnSettings.SpawnerData>builder()
                                .add(new MobSpawnSettings.SpawnerData(AllEntityTypes.UNFORGIVEN_PERFIDY.get(), 1, 1), 10)
                                .add(new MobSpawnSettings.SpawnerData(AllEntityTypes.UNFORGIVEN_RIDICULE.get(), 1, 1), 10)
                                .add(new MobSpawnSettings.SpawnerData(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(), 1, 1), 10)
                                .add(new MobSpawnSettings.SpawnerData(AllEntityTypes.UNFORGIVEN_COWARDICE.get(), 1, 4), 10)
                                .add(new MobSpawnSettings.SpawnerData(AllEntityTypes.UNFORGIVEN_SPOILING.get(), 1, 2), 10).build()
                ));
        context.register(ADD_UNFORGIVEN_MONSTERS_COST,
                new BiomeModifiers.AddSpawnCostsBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.DEEP_DARK)),
                        HolderSet.direct(AllEntityTypes.UNFORGIVEN_COWARDICE,
                                AllEntityTypes.UNFORGIVEN_PERFIDY,
                                AllEntityTypes.UNFORGIVEN_INDISCRETION,
                                AllEntityTypes.UNFORGIVEN_RIDICULE,
                                AllEntityTypes.UNFORGIVEN_SPOILING),
                        new MobSpawnSettings.MobSpawnCost(0.0625, 1)
                ));
    }
}
