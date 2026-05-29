package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class MySoundDefinitionsProvider extends SoundDefinitionsProvider {
    public MySoundDefinitionsProvider(PackOutput output) {
        super(output, LanfasieBenderson.MODID);
    }

    @Override
    public void registerSounds() {
        add(AllSoundEvents.LETHAL_ATTACK_SFX, SoundDefinition.definition()
                .with(sound(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "omen/lethal_attack_sfx"), SoundDefinition.SoundType.SOUND)
                        .volume(1.0f)
                        .pitch(1.0f).weight(5).attenuationDistance(8))
                .subtitle("sound.%s.lethal_attack.subtitle".formatted(LanfasieBenderson.MODID)));
        add(AllSoundEvents.STACK_ATTACK_SFX, SoundDefinition.definition()
                .with(sound(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "omen/stack_attack_sfx"), SoundDefinition.SoundType.SOUND)
                        .volume(1.0f)
                        .pitch(1.0f).weight(5).attenuationDistance(16))
                .subtitle("sound.%s.stack_attack.subtitle".formatted(LanfasieBenderson.MODID)));
    }
}
