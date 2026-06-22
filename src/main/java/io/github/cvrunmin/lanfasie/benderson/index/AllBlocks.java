package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.statue.EndGuardianStatueBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LanfasieBenderson.MODID);

    public static final DeferredBlock<Block> DEEP_LATENT_BLOCK = BLOCKS.registerBlock("deep_latent_block", prop -> new Block(prop.destroyTime(2).lightLevel(_ -> 2).explosionResistance(20).noLootTable()));
    public static final DeferredBlock<Block> DEEP_LATENT_CALLER = BLOCKS.registerBlock("deep_latent_caller", prop -> new Block(prop.destroyTime(5).lightLevel(_ -> 2).explosionResistance(1000)));
    public static final DeferredBlock<EndGuardianStatueBlock> END_GUARDIAN_STATUE = BLOCKS.registerBlock("end_guardian_statue", prop ->
            new EndGuardianStatueBlock(
                    prop.strength(3.0f, 5.0f)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static void register(IEventBus modBus){
        BLOCKS.register(modBus);
    }
}
