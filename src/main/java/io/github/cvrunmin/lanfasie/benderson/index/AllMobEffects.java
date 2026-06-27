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
            .addAttributeModifier(Attributes.GRAVITY, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "effect.void_hare"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

    public static final DeferredHolder<MobEffect, SummerSeptetMobEffect> SUMMER_SEPTET = MOB_EFFECT.register("summer_septet", () -> new SummerSeptetMobEffect(MobEffectCategory.NEUTRAL));
    public static final DeferredHolder<MobEffect, MundaneTrioMobEffect> MUNDANE_TRIO = MOB_EFFECT.register("mundane_trio", () -> new MundaneTrioMobEffect(MobEffectCategory.NEUTRAL));
    public static final DeferredHolder<MobEffect, MundaneTrioMobEffect.MundaneTrioBigOrangeMobEffect> OPENING_MINUET = MOB_EFFECT.register("opening_minuet", () ->
            ((MundaneTrioMobEffect.MundaneTrioBigOrangeMobEffect) new MundaneTrioMobEffect.MundaneTrioBigOrangeMobEffect(MobEffectCategory.BENEFICIAL).withSoundOnAdded(AllSoundEvents.OPENING_MINUET_SFX.get())));
    public static final DeferredHolder<MobEffect, MundaneTrioMobEffect.MundaneTrioLittleOrangeMobEffect> TWIN_BALLAD = MOB_EFFECT.register("twin_ballad", () ->
            ((MundaneTrioMobEffect.MundaneTrioLittleOrangeMobEffect) new MundaneTrioMobEffect.MundaneTrioLittleOrangeMobEffect(MobEffectCategory.BENEFICIAL).withSoundOnAdded(AllSoundEvents.TWIN_BALLAD_SFX.get())));
    public static final DeferredHolder<MobEffect, MundaneTrioMobEffect.MundaneTrioPentaMobEffect> BELOVED_PAEAN = MOB_EFFECT.register("beloved_paean", () ->
            ((MundaneTrioMobEffect.MundaneTrioPentaMobEffect) new MundaneTrioMobEffect.MundaneTrioPentaMobEffect(MobEffectCategory.BENEFICIAL).withSoundOnAdded(AllSoundEvents.BELOVED_PAEAN_SFX.get())));

    public static void register(IEventBus modBus){
        MOB_EFFECT.register(modBus);
    }

}
