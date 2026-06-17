package io.github.cvrunmin.lanfasie.benderson;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BENDERSON_NORMAL_ATTACK_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of normal attack dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_normal_attack_damage_multiplier")
                    .defineInRange("benderson_normal_attack_damage_multiplier", 1.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_CIRCLE_STACK_ATTACK_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of circular stackable AoE dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_circular_stackable_aoe_damage_multiplier")
                    .defineInRange("benderson_circular_stackable_aoe_damage_multiplier", 20.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_CIRCLE_AOE_SELF_ATTACK_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of self-surrounding circular AoE dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_self_circular_aoe_damage_multiplier")
                    .defineInRange("benderson_self_circular_aoe_damage_multiplier", 22.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_CENTER_KNOCKBACKING_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of center-knockbacking dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_center_knockbacking_damage_multiplier")
                    .defineInRange("benderson_center_knockbacking_damage_multiplier", 2.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_SWEEP_PARTIAL_ARENA_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of partial arena sweeping dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_partial_sweep_arena_damage_multiplier")
                    .defineInRange("benderson_partial_sweep_arena_damage_multiplier", 22.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_PRE_ECLIPTIC_PILE_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of pre-ecliptic-meteor piles falling damage dealt by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_pre_ecliptic_pile_damage_multiplier")
                    .defineInRange("benderson_pre_ecliptic_pile_damage_multiplier", 10.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_CAT_SMASHING_ATTACK_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of cat smashing summoned by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_cat_smashing_attack_damage_multiplier")
                    .defineInRange("benderson_cat_smashing_attack_damage_multiplier", 20.0, 1.0, Double.MAX_VALUE);
    public static final ModConfigSpec.DoubleValue BENDERSON_FIREBALL_METEOR_ATTACK_DAMAGE_MULTIPLIER =
            BUILDER.comment("Damage multiplier of fireball meteor summoned by Benderson")
                    .worldRestart()
                    .translation("config.lanfasie_benderson.benderson_fireball_meteor_attack_damage_multiplier")
                    .defineInRange("benderson_fireball_meteor_attack_damage_multiplier", 15.0, 1.0, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
