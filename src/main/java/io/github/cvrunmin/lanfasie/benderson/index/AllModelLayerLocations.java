package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.equipment.ShallowayShieldModel;
import io.github.cvrunmin.lanfasie.benderson.content.unforgiven.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class AllModelLayerLocations {
    public static final ModelLayerLocation UNFORGIVEN_COWARDICE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "unforgiven_cowardice"), "main");
    public static final ModelLayerLocation UNFORGIVEN_INDISCRETION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "unforgiven_indiscretion"), "main");
    public static final ModelLayerLocation UNFORGIVEN_PERFIDY = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "unforgiven_perfidy"), "main");
    public static final ModelLayerLocation UNFORGIVEN_RIDICULE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "unforgiven_ridicule"), "main");
    public static final ModelLayerLocation UNFORGIVEN_SPOILING = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "unforgiven_spoiling"), "main");
    public static final ModelLayerLocation SHALLOWAY_SHIELD = new ModelLayerLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "shalloway_shield"), "main");

    @SubscribeEvent
    public static void registerModelLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(UNFORGIVEN_COWARDICE, UnforgivenCowardiceModel::createBodyLayer);
        event.registerLayerDefinition(UNFORGIVEN_INDISCRETION, UnforgivenIndiscretionModel::createBodyLayer);
        event.registerLayerDefinition(UNFORGIVEN_PERFIDY, UnforgivenPerfidyModel::createBodyLayer);
        event.registerLayerDefinition(UNFORGIVEN_RIDICULE, UnforgivenRidiculeModel::createBodyLayer);
        event.registerLayerDefinition(UNFORGIVEN_SPOILING, UnforgivenSpoilingModel::createBodyLayer);
        event.registerLayerDefinition(SHALLOWAY_SHIELD, ShallowayShieldModel::createLayer);
    }
}
