package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, LanfasieBenderson.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LETHAL_ATTACK_SFX = SOUND_EVENTS.register("lethal_attack_sfx", SoundEvent::createVariableRangeEvent);
    public static final DeferredHolder<SoundEvent, SoundEvent> STACK_ATTACK_SFX = SOUND_EVENTS.register("stack_attack_sfx", SoundEvent::createVariableRangeEvent);

    public static void register(IEventBus modBus){
        SOUND_EVENTS.register(modBus);
    }

}
