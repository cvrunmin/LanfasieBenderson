package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.data.MyDamageTypeTagsProvider;
import io.github.cvrunmin.lanfasie.benderson.index.*;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(LanfasieBenderson.MODID)
public class LanfasieBenderson {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lanfasie_benderson";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public LanfasieBenderson(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        AllDataComponents.register(modEventBus);
        AllBlocks.register(modEventBus);
        AllItems.register(modEventBus);
        AllAttributes.register(modEventBus);
        AllEntityTypes.register(modEventBus);
        AllMobEffects.register(modEventBus);
        AllCreativeModeTabs.register(modEventBus);
        AllEntityDataSerializers.register(modEventBus);
        AllSoundEvents.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (LanfasieBenderson) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
//        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::modifyDefaultAttribute);

//        modEventBus.addListener(this::gatherData);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(CreativeModeTabs.COMBAT.equals(event.getTabKey())){
            event.accept(AllItems.SWORD_OF_DAWNWAITER);
            event.accept(AllItems.PROVOKING_STICK);
        }
        if(CreativeModeTabs.INGREDIENTS.equals(event.getTabKey())){
            event.accept(AllItems.OMINOUS_ORB);
        }
        if(CreativeModeTabs.FUNCTIONAL_BLOCKS.equals(event.getTabKey())){
            event.accept(AllItems.DEEP_LATENT_CALLER);
        }
        if(CreativeModeTabs.SPAWN_EGGS.equals(event.getTabKey())){
            event.accept(AllItems.UNFORGIVEN_COWARDICE_SPAWN_EGG);
            event.accept(AllItems.UNFORGIVEN_SPOILING_SPAWN_EGG);
            event.accept(AllItems.UNFORGIVEN_INDISCRETION_SPAWN_EGG);
            event.accept(AllItems.UNFORGIVEN_PERFIDY_SPAWN_EGG);
            event.accept(AllItems.UNFORGIVEN_RIDICULE_SPAWN_EGG);
        }
    }

    private void modifyDefaultAttribute(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, AllAttributes.ENMITY_MULTIPLIER);
    }

//    private void gatherData(GatherDataEvent.Server event){
//        event.createDatapackRegistryObjects(new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, AllDamageTypes::bootstrap));
//        event.createProvider(MyDamageTypeTagsProvider::new);
//    }
}
