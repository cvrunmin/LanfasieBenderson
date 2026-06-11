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
        add(AllSoundEvents.BENDERSON_BOSS_THEME_1, SoundDefinition.definition()
                .with(sound(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "music/stack_of_unforgetable"), SoundDefinition.SoundType.SOUND)
                        .volume(0.25f).pitch(1.0)));
        add(AllSoundEvents.LETHAL_ATTACK_SFX, SoundDefinition.definition()
                .with(sound(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "omen/lethal_attack_sfx"), SoundDefinition.SoundType.SOUND)
                        .volume(1.0f)
                        .pitch(1.0f).weight(5).attenuationDistance(8))
                .subtitle("subtitles.%s.lethal_attack".formatted(LanfasieBenderson.MODID)));
        add(AllSoundEvents.STACK_ATTACK_SFX, SoundDefinition.definition()
                .with(sound(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "omen/stack_attack_sfx"), SoundDefinition.SoundType.SOUND)
                        .volume(1.0f)
                        .pitch(1.0f).weight(5).attenuationDistance(16))
                .subtitle("subtitles.%s.stack_attack".formatted(LanfasieBenderson.MODID)));
        add(AllSoundEvents.BOSS_SWEEP_SFX, SoundDefinition.definition()
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep1"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep2"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep3"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep4"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep5"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep6"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .with(
                        sound(Identifier.withDefaultNamespace("entity/player/attack/sweep7"), SoundDefinition.SoundType.SOUND)
                        .volume(.7).attenuationDistance(32)
                )
                .subtitle("subtitles.%s.enemy_sweep".formatted(LanfasieBenderson.MODID)));
    }
}
