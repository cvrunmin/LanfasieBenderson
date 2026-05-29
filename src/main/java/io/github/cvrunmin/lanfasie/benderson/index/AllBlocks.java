package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LanfasieBenderson.MODID);

    public static final DeferredHolder<Block, Block> DEEP_LATENT_CALLER = BLOCKS.registerBlock("deep_latent_caller", prop -> new Block(prop.destroyTime(5).lightLevel(_ -> 2).explosionResistance(1000)));
    public static void register(IEventBus modBus){
        BLOCKS.register(modBus);
    }
}
