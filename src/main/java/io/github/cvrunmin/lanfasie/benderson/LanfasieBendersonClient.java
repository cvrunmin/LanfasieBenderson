package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.marker.AttackTargetMarkerRenderer;
import io.github.cvrunmin.lanfasie.benderson.data.MyLanguageProvider;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = LanfasieBenderson.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class LanfasieBendersonClient {
    public LanfasieBendersonClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(AllEntityTypes.BENDERSON.get(), ctx -> new BendersonRenderer<>(ctx, AllEntityTypes.BENDERSON.get()));
        event.registerEntityRenderer(AllEntityTypes.ATTACK_TARGET_MARKER.get(), AttackTargetMarkerRenderer::new);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){
        event.createProvider(MyLanguageProvider::new);
    }
}
