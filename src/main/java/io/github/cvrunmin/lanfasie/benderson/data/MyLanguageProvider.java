package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MyLanguageProvider extends LanguageProvider {
    public MyLanguageProvider(PackOutput output) {
        super(output, LanfasieBenderson.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.addItem(AllItems.SWORD_OF_DAWNWAITER, "Sword of Dawnwaiter");
        this.addItem(AllItems.SWORD_OF_DAWNWAITER_TAINTED, "Tainted Sword of Dawnwaiter");
        this.addItem(AllItems.OMINOUS_ORB, "Ominous Orb of Latent");
        this.addItem(AllItems.PROVOKING_STICK, "Provoking Stick");
        this.addBlock(AllBlocks.DEEP_LATENT_CALLER, "Deep Latent Caller");
        this.add("entity.lanfasie_benderson.benderson", "Benderson the Abyss-sunken Dawnwaiter");
        this.add("entity.lanfasie_benderson.benderson.name.deep_latent", "Team Soul of Deep Latent");
        this.add("entity.lanfasie_benderson.benderson.name.unforgiven", "Unforgiven Team Soul");
        this.add("entity.lanfasie_benderson.benderson.name.forgiven", "Forgiven Team Soul");
        this.add("entity.lanfasie_benderson.target_marker", "Target Marker");
        this.add("entity.lanfasie_benderson.anticalabrum", "Anticalabrum");
        this.add("sound.lanfasie_benderson.lethal_attack.subtitle", "Omen of lethal attack");
        this.add("sound.lanfasie_benderson.stack_attack.subtitle", "Omen of stack attack");
        this.addEffect(AllMobEffects.AGGRO_UP, "Provoking");
        this.addEffect(AllMobEffects.VULNERABILITY_UP, "Vulnerability Up");
        this.addEffect(AllMobEffects.CURSE_BLACK_CAT, "Anticalabrum: Felis Invisibilis");
        this.addEffect(AllMobEffects.CURSE_END_GUARDIAN, "Anticalabrum: End Guardian");
        this.addEffect(AllMobEffects.CURSE_HYDRO_DREAMER, "Anticalabrum: Hydrous Dreamer");
        this.addEffect(AllMobEffects.CURSE_NETHER_DOG, "Anticalabrum: Nether Cerberus");
        this.addEffect(AllMobEffects.CURSE_VOID_HARE, "Anticalabrum: Void Hare");
        this.add("death.attack.lethal_attack", "%1$s could not survive from high damage of %2$s");
        this.add("death.attack.lethal_attack.player", "%1$s could not survive from high damage of %2$s");
        this.add("death.attack.lethal_attack.item", "%1$s could not survive from high damage of %2$s using %3$s");
        this.add("death.attack.boss_ability_attack", "%1$s was one step behind from safe area when dodging attack from %2$s");
        this.add("death.attack.boss_ability_attack.player", "%1$s was one step behind from safe area when dodging attack from %2$s");
        this.add("death.attack.boss_ability_attack.item", "%1$s was one step behind from safe area when dodging attack from %2$s holding %3$s");
        this.add("text.lanfasie_benderson.enmity_bar.aggro", "A");
    }
}
