package io.github.cvrunmin.lanfasie.benderson.index;

import com.mojang.serialization.Codec;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AllDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LanfasieBenderson.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> POETIC_SOUL = ATTACHMENT_TYPES.register("poetic_soul",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("poetic_heart")).build());

    public static void register(IEventBus modBus){
        ATTACHMENT_TYPES.register(modBus);
    }
}
