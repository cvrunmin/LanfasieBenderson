package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.compat.iris.IrisCompatEntry;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.AnticalabrumModel;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.AnticalabrumRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.BendersonRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.dawn.DawnEntityRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.equipment.ShallowayShieldSpecialRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarkerRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarkerRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserBardRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserRedMageRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserWhiteMageRenderer;
import io.github.cvrunmin.lanfasie.benderson.content.particles.BlockDustSuckingBlowingParticleEmitter;
import io.github.cvrunmin.lanfasie.benderson.content.particles.DustSuckingBlowingParticleEmitter;
import io.github.cvrunmin.lanfasie.benderson.content.unforgiven.*;
import io.github.cvrunmin.lanfasie.benderson.data.*;
import io.github.cvrunmin.lanfasie.benderson.foundation.HeiTideSkyboxRenderer;
import io.github.cvrunmin.lanfasie.benderson.foundation.MyGlobalRenderPipelines;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import io.github.cvrunmin.lanfasie.benderson.index.AllParticleTypes;
import io.github.cvrunmin.lanfasie.benderson.utils.ArmPoseEnumProxy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = LanfasieBenderson.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class LanfasieBendersonClient {
    public LanfasieBendersonClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        IrisCompatEntry.tryCompat();
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(AllEntityTypes.BENDERSON.get(), BendersonRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.PROJECTED_BENDERSON.get(), BendersonRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.TARGET_MARKER.get(), TargetMarkerRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.ANTICALABRUM.get(), AnticalabrumRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.DAWN.get(), DawnEntityRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.MUNDANE_PRAISER_BARD.get(), MundanePraiserBardRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.MUNDANE_PRAISER_WHITE_MAGE.get(), MundanePraiserWhiteMageRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.MUNDANE_PRAISER_RED_MAGE.get(), MundanePraiserRedMageRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.DELAYED_ATTACK_MARKER.get(), DelayedAttackMarkerRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.UNFORGIVEN_COWARDICE.get(), UnforgivenCowardiceRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.UNFORGIVEN_INDISCRETION.get(), UnforgivenIndiscretionRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.UNFORGIVEN_PERFIDY.get(), UnforgivenPerfidyRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.UNFORGIVEN_RIDICULE.get(), UnforgivenRidiculeRenderer::new);
        event.registerEntityRenderer(AllEntityTypes.UNFORGIVEN_SPOILING.get(), UnforgivenSpoilingRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event){
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ArmPoseEnumProxy.MANA_FOCI_POSE_PROXY.getValue();
            }
        }, AllItems.MUNDANE_PRAISER_MANA_FOCI);
    }

    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event){
        event.register(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "shalloway_shield"), ShallowayShieldSpecialRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerStandaloneModel(ModelEvent.RegisterStandalone event){
        event.register(AnticalabrumModel.MODEL_KEY, AnticalabrumModel.getBaker());
    }

    @SubscribeEvent
    public static void registerConditionalModelProperty(RegisterConditionalItemModelPropertyEvent event){
        event.register(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "is_provoking"), IsProvokingModelProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerCustomPipelines(RegisterRenderPipelinesEvent event){
        event.registerPipeline(MyGlobalRenderPipelines.ISOLATED_SKY);
        event.registerPipeline(MyGlobalRenderPipelines.ISOLATED_SKY_TRANSLUCENT);
        event.registerPipeline(MyGlobalRenderPipelines.ENTITY_SOLID_TRIANGLE);
        event.registerPipeline(TargetMarkerRenderer.ATTACK_TARGET_MARKER);
        event.registerPipeline(TargetMarkerRenderer.ATTACK_TARGET_MARKER_TRIANGLE_STRIP);
        event.registerPipeline(HeiTideSkyboxRenderer.EYES);
        event.registerPipeline(BendersonRenderer.PORTAL_PIPELINE);
        event.registerPipeline(BendersonRenderer.PORTAL_TRIANGLE_PIPELINE);
    }

    @SubscribeEvent
    public static void registerParticleProvider(RegisterParticleProvidersEvent event){
        event.registerSpecial(AllParticleTypes.BLOCK_DUST_SUCKING.get(), new BlockDustSuckingBlowingParticleEmitter.SuckingProvider());
        event.registerSpecial(AllParticleTypes.BLOCK_DUST_BLOWING.get(), new BlockDustSuckingBlowingParticleEmitter.BlowingProvider());
        event.registerSpecial(AllParticleTypes.DUST_BLOWING.get(), new DustSuckingBlowingParticleEmitter.BlowingProvider());
        event.registerSpecial(AllParticleTypes.DUST_SUCKING.get(), new DustSuckingBlowingParticleEmitter.SuckingProvider());
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){
        // from server side
        event.createDatapackRegistryObjects(new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, AllDamageTypes::bootstrap));
        event.createProvider((output, lookupProvider) ->
                new LootTableProvider(output,
                        Set.of(),
                        List.of(new LootTableProvider.SubProviderEntry(MyBlockLootTableSubProvider::new, LootContextParamSets.BLOCK),
                                new LootTableProvider.SubProviderEntry(MyCustomLootTableSubProvider::new, LootContextParamSets.ENTITY)),
                        lookupProvider));
        // client side
        event.createProvider(MyModelProvider::new);
        event.createProvider(MyBlockTagsProvider::new);
        event.createProvider(MyItemTagsProvider::new);
        event.createProvider(MyEntityTypeTagsProvider::new);
        event.createProvider(MyDamageTypeTagsProvider::new);
        event.createProvider(MyLanguageProvider::new);
        event.createProvider(MySoundDefinitionsProvider::new);
    }
}
