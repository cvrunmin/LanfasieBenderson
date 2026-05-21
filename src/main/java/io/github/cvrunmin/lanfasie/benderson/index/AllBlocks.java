package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LanfasieBenderson.MODID);
    public static void register(IEventBus modBus){
        BLOCKS.register(modBus);
    }
}
