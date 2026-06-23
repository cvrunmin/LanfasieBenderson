package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LanfasieBenderson.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MY_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.lanfasie_benderson")) //The language key for the title of your CreativeModeTab
        .icon(() -> AllItems.AGGRO_UP_ICON.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(AllItems.DEEP_LATENT_CALLER);
            output.accept(AllItems.OMINOUS_ORB);
            output.accept(AllItems.PROVOKING_STICK);
            output.accept(AllItems.SWORD_OF_DAWNWAITER);
            output.accept(AllItems.SHALLOWAY_SWORD);
            output.accept(AllItems.SHALLOWAY_SHIELD);
            output.accept(AllItems.MUNDANE_PRAISER_BOW);
            output.accept(AllItems.MUNDANE_PRAISER_CANE);
            output.accept(AllItems.MUNDANE_PRAISER_RAPIER);
            output.accept(AllItems.MUNDANE_PRAISER_MANA_FOCI);
            output.accept(AllItems.DAWNWAITER_TOTEM);
            output.accept(AllItems.END_GUARDIAN_STATUE);
            output.accept(AllItems.UNFORGIVEN_PERFIDY_SPAWN_EGG);
            output.accept(AllItems.UNFORGIVEN_RIDICULE_SPAWN_EGG);
            output.accept(AllItems.UNFORGIVEN_INDISCRETION_SPAWN_EGG);
            output.accept(AllItems.UNFORGIVEN_COWARDICE_SPAWN_EGG);
            output.accept(AllItems.UNFORGIVEN_SPOILING_SPAWN_EGG);
        }).build());

    public static void register(IEventBus modBus){
        CREATIVE_MODE_TABS.register(modBus);
    }
}
