package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.HeiTideSkyboxRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterCustomEnvironmentEffectRendererEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class AllCustomEnvRenderer {
    public static final Identifier HEI_TIDE_SKY_RENDERER = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "hei_tide_sky");
    public static final Identifier RELOAD_HEI_TIDE_SKY_RENDERER = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "reload_hei_tide_sky_resources");

    private static HeiTideSkyboxRenderer heiTideSkyboxRenderer;

    @SubscribeEvent
    public static void registerCustomEnvironmentRenderer(RegisterCustomEnvironmentEffectRendererEvent event){
        if(heiTideSkyboxRenderer == null)
            heiTideSkyboxRenderer = new HeiTideSkyboxRenderer();
        event.registerSkyboxRenderer(HEI_TIDE_SKY_RENDERER, heiTideSkyboxRenderer);
    }

    @SubscribeEvent
    public static void addClientResourceReloadListener(AddClientReloadListenersEvent event){
        event.addListener(RELOAD_HEI_TIDE_SKY_RENDERER, (ResourceManagerReloadListener) resourceManager -> heiTideSkyboxRenderer.reloadResources());
    }

    @SubscribeEvent
    public static void onGameClosed(ClientStoppingEvent event){
        heiTideSkyboxRenderer.close();
    }
}
