package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.content.statue.EndGuardianStatueBlock;
import io.github.cvrunmin.lanfasie.benderson.content.statue.FiveGuysStatueBlock;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Set;

public class MyBlockLootTableSubProvider extends BlockLootSubProvider {
    public MyBlockLootTableSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return AllBlocks.BLOCKS.getEntries().stream().map(e -> ((Block) e.value())).toList();
    }

    @Override
    protected void generate() {
        this.dropSelf(AllBlocks.DEEP_LATENT_CALLER.get());
        this.add(AllBlocks.END_GUARDIAN_STATUE.get(), this.createSinglePropConditionTable(AllBlocks.END_GUARDIAN_STATUE.get(), FiveGuysStatueBlock.HALF, DoubleBlockHalf.LOWER));
        this.add(AllBlocks.FELIS_INVISIBILIS_STATUE.get(), this.createSinglePropConditionTable(AllBlocks.FELIS_INVISIBILIS_STATUE.get(), FiveGuysStatueBlock.HALF, DoubleBlockHalf.LOWER));
        this.add(AllBlocks.NETHER_DOG_STATUE.get(), this.createSinglePropConditionTable(AllBlocks.NETHER_DOG_STATUE.get(), FiveGuysStatueBlock.HALF, DoubleBlockHalf.LOWER));
        this.add(AllBlocks.HYDRO_DREAMER_STATUE.get(), this.createSinglePropConditionTable(AllBlocks.HYDRO_DREAMER_STATUE.get(), FiveGuysStatueBlock.HALF, DoubleBlockHalf.LOWER));
        this.add(AllBlocks.VOID_HARE_STATUE.get(), this.createSinglePropConditionTable(AllBlocks.VOID_HARE_STATUE.get(), FiveGuysStatueBlock.HALF, DoubleBlockHalf.LOWER));
    }
}
