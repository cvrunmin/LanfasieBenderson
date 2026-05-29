package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class AllDamageTypes {
    public static final ResourceKey<DamageType> LETHAL_ATTACK = create("lethal_attack");
    public static final ResourceKey<DamageType> BOSS_NORMAL_ATTACK = create("boss_normal_attack");
    public static final ResourceKey<DamageType> BOSS_ABILITY_ATTACK = create("boss_ability_attack");

    private static ResourceKey<DamageType> create(String path){
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, path));
    }

    public static void bootstrap(BootstrapContext<DamageType> bootstrapContext){
        bootstrapContext.register(LETHAL_ATTACK, new DamageType("lethal_attack", DamageScaling.NEVER, 0.2f));
        bootstrapContext.register(BOSS_NORMAL_ATTACK, new DamageType("mob", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
        bootstrapContext.register(BOSS_ABILITY_ATTACK, new DamageType("boss_ability_attack", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.05f));
    }
}
