package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
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
        this.addItem(AllItems.CLAYMORE_OF_HEI_POWER, "Claymore of Hei Power");
        this.addItem(AllItems.DAWNWAITER_TOTEM, "Dawnwaiter Totem");
        this.addItem(AllItems.OMINOUS_ORB, "Ominous Orb of Latent");
        this.addItem(AllItems.PROVOKING_STICK, "Provoking Stick");
        this.addItem(AllItems.UNFORGIVEN_COWARDICE_SPAWN_EGG, "Unforgiven Cowardice Spawn Egg");
        this.addItem(AllItems.UNFORGIVEN_INDISCRETION_SPAWN_EGG, "Unforgiven Indiscretion Spawn Egg");
        this.addItem(AllItems.UNFORGIVEN_PERFIDY_SPAWN_EGG, "Unforgiven Perfidy Spawn Egg");
        this.addItem(AllItems.UNFORGIVEN_RIDICULE_SPAWN_EGG, "Unforgiven Ridicule Spawn Egg");
        this.addItem(AllItems.UNFORGIVEN_SPOILING_SPAWN_EGG, "Unforgiven Spoiling Spawn Egg");
        this.addItem(AllItems.SHALLOWAY_SWORD, "Shalloway Sword");
        this.addItem(AllItems.SHALLOWAY_SHIELD, "Shalloway Shield");
        this.addItem(AllItems.MUNDANE_PRAISER_BOW, "Mundane Praiser Bow");
        this.addItem(AllItems.MUNDANE_PRAISER_CANE, "Mundane Praiser Cane");
        this.addItem(AllItems.MUNDANE_PRAISER_RAPIER, "Mundane Praiser Rapier");
        this.addItem(AllItems.MUNDANE_PRAISER_MANA_FOCI, "Mundane Praiser Mana Foci");
        this.addBlock(AllBlocks.DEEP_LATENT_BLOCK, "Deep Latent Block");
        this.addBlock(AllBlocks.DEEP_LATENT_CALLER, "Deep Latent Caller");
        this.addBlock(AllBlocks.END_GUARDIAN_STATUE, "End Guardian Statue");
        this.add("itemGroup.lanfasie_benderson", "Lanfasie: Benderson");
        this.addEntityType(AllEntityTypes.LANFASIE, "Phantom Lanfasie");
        this.addEntityType(AllEntityTypes.BENDERSON, "Benderson the Abyss-sunken Dawnwaiter");
        this.addEntityType(AllEntityTypes.PROJECTED_BENDERSON, "Benderson the Abyss-sunken Dawnwaiter (Projected)");
        this.add("entity.lanfasie_benderson.benderson.name.deep_latent", "Team Soul of Deep Latent");
        this.add("entity.lanfasie_benderson.benderson.name.unforgiven", "Unforgiven Team Soul");
        this.add("entity.lanfasie_benderson.benderson.name.forgiven", "Forgiven Team Soul");
        this.add("misc.lanfasie_benderson.projected_suffix", "«Projected»");
        this.addEntityType(AllEntityTypes.TARGET_MARKER, "Target Marker");
        this.addEntityType(AllEntityTypes.DELAYED_ATTACK_MARKER, "Delayed Attack Marker");
        this.addEntityType(AllEntityTypes.ANTICALABRUM, "Anticalabrum");
        this.addEntityType(AllEntityTypes.DAWN, "Dawn");
        this.addEntityType(AllEntityTypes.MUNDANE_PRAISER_BARD, "Mundane Praiser Bard");
        this.addEntityType(AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE, "Mundane Praiser White Mage");
        this.addEntityType(AllEntityTypes.MUNDANE_PRAISER_RED_MAGE, "Mundane Praiser Red Mage");
        this.addEntityType(AllEntityTypes.UNFORGIVEN_SPOILING, "Unforgiven Spoiling");
        this.addEntityType(AllEntityTypes.UNFORGIVEN_RIDICULE, "Unforgiven Ridicule");
        this.addEntityType(AllEntityTypes.UNFORGIVEN_PERFIDY, "Unforgiven Perfidy");
        this.addEntityType(AllEntityTypes.UNFORGIVEN_COWARDICE, "Unforgiven Cowardice");
        this.addEntityType(AllEntityTypes.UNFORGIVEN_INDISCRETION, "Unforgiven Indiscretion");
        this.add("subtitles.lanfasie_benderson.lethal_attack", "Omen of lethal attack");
        this.add("subtitles.lanfasie_benderson.stack_attack", "Omen of stack attack");
        this.add("subtitles.lanfasie_benderson.enemy_sweep", "Sweeping attack");
        this.add("subtitles.lanfasie_benderson.opening_minuet", "Opening Minuet");
        this.add("subtitles.lanfasie_benderson.twin_ballad", "Twin Ballad");
        this.add("subtitles.lanfasie_benderson.beloved_paean", "Beloved Paean");
        this.addItem(AllItems.AGGRO_UP_ICON, "Provoking");
        this.addEffect(AllMobEffects.AGGRO_UP, "Provoking");
        this.addEffect(AllMobEffects.VULNERABILITY_UP, "Vulnerability Up");
        this.addEffect(AllMobEffects.CURSE_BLACK_CAT, "Anticalabrum: Felis Invisibilis");
        this.addEffect(AllMobEffects.CURSE_END_GUARDIAN, "Anticalabrum: End Guardian");
        this.addEffect(AllMobEffects.CURSE_HYDRO_DREAMER, "Anticalabrum: Hydrous Dreamer");
        this.addEffect(AllMobEffects.CURSE_NETHER_DOG, "Anticalabrum: Nether Cerberus");
        this.addEffect(AllMobEffects.CURSE_VOID_HARE, "Anticalabrum: Void Hare");
        this.addEffect(AllMobEffects.SUMMER_SEPTET, "Summer Septet");
        this.addEffect(AllMobEffects.MUNDANE_TRIO, "Mundane Trio");
        this.addEffect(AllMobEffects.OPENING_MINUET, "Opening Minuet (Mundane Trio)");
        this.addEffect(AllMobEffects.TWIN_BALLAD, "Twin Ballad (Mundane Trio)");
        this.addEffect(AllMobEffects.BELOVED_PAEAN, "Beloved's Paean (Mundane Trio)");
        this.add("death.attack.lethal_attack", "%1$s could not survive from high damage of %2$s");
        this.add("death.attack.lethal_attack.player", "%1$s could not survive from high damage of %2$s");
        this.add("death.attack.lethal_attack.item", "%1$s could not survive from high damage of %2$s using %3$s");
        this.add("death.attack.boss_ability_attack", "%1$s was one step behind from safe area when dodging attack from %2$s");
        this.add("death.attack.boss_ability_attack.player", "%1$s was one step behind from safe area when dodging attack from %2$s");
        this.add("death.attack.boss_ability_attack.item", "%1$s was one step behind from safe area when dodging attack from %2$s holding %3$s");
        this.add("death.attack.ecliptic_meteor", "%1$s ordered an Ecliptic Meteorite Latte, and made a default review for %2$s");
        this.add("death.attack.ecliptic_meteor.player", "%1$s ordered an Ecliptic Meteorite Latte, and made a default review for %2$s");
        this.add("death.attack.ecliptic_meteor.item", "%1$s ordered an Ecliptic Meteorite Latte, and made a default review for %2$s holding %3$s");
        this.add("text.lanfasie_benderson.enmity_bar.aggro", "A");
        this.add("attributes.lanfasie_benderson.enmity_multiplier", "Enmity multiplier");
        this.add("attributes.lanfasie_benderson.damage_gate", "Damage Gate Percentage");
        this.add("attributes.lanfasie_benderson.extreme", "Extreme Mode");
        this.add("config.lanfasie_benderson.benderson_normal_attack_damage_multiplier", "Benderson Normal Attack Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_circular_stackable_aoe_damage_multiplier", "Benderson Circular Stackable AoE Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_self_circular_aoe_damage_multiplier", "Benderson Self Circular AoE Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_center_knockbacking_damage_multiplier", "Benderson Center Knockbacking Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_partial_sweep_arena_damage_multiplier", "Benderson Partial Sweep Arena Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_pre_ecliptic_pile_damage_multiplier", "Benderson Pre-Ecliptic Pile Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_cat_smashing_attack_damage_multiplier", "Benderson Cat Smashing Attack Damage Multiplier");
        this.add("config.lanfasie_benderson.benderson_fireball_meteor_attack_damage_multiplier", "Benderson Fireball Meteor Attack Damage Multiplier");
    }
}
