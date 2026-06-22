package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class MyBlockTagsProvider extends KeyTagProvider<Block> {
    public MyBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BLOCK, lookupProvider, LanfasieBenderson.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AllBlocks.END_GUARDIAN_STATUE.getKey(), AllBlocks.DEEP_LATENT_BLOCK.getKey(), AllBlocks.DEEP_LATENT_CALLER.getKey());
    }
}
