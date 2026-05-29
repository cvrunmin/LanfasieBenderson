package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.effects.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AllMobEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(Registries.MOB_EFFECT, LanfasieBenderson.MODID);

    public static final DeferredHolder<MobEffect, AggroUpMobEffect> AGGRO_UP = MOB_EFFECT.register("aggro_up", () -> ((AggroUpMobEffect) new AggroUpMobEffect(MobEffectCategory.BENEFICIAL, 0)
            .addAttributeModifier(AllAttributes.ENMITY_MULTIPLIER, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "effect.aggro_up"), 10, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<MobEffect, VulnerabilityUpMobEffect> VULNERABILITY_UP = MOB_EFFECT.register("vulnerability_up", () -> new VulnerabilityUpMobEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, CurseHydroDreamerMobEffect> CURSE_HYDRO_DREAMER = MOB_EFFECT.register("curse_hydro_dreamer", () -> new CurseHydroDreamerMobEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, CurseBlackCatMobEffect> CURSE_BLACK_CAT = MOB_EFFECT.register("curse_black_cat", () -> new CurseBlackCatMobEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, CurseEndGuardianMobEffect> CURSE_END_GUARDIAN = MOB_EFFECT.register("curse_end_guardian", () -> new CurseEndGuardianMobEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, CurseNetherDogMobEffect> CURSE_NETHER_DOG = MOB_EFFECT.register("curse_nether_dog", () -> new CurseNetherDogMobEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, CurseVoidHareMobEffect> CURSE_VOID_HARE = MOB_EFFECT.register("curse_void_hare", () -> ((CurseVoidHareMobEffect) new CurseVoidHareMobEffect(MobEffectCategory.HARMFUL, 0)
            .addAttributeModifier(Attributes.GRAVITY, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "effect.void_hare"), -0.9, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

    public static void register(IEventBus modBus){
        MOB_EFFECT.register(modBus);
    }

}
