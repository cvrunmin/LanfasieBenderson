package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.dawn.DawnEntity;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserBard;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserRedMage;
import io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers.MundanePraiserWhiteMage;
import io.github.cvrunmin.lanfasie.benderson.content.unforgiven.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AllEntityTypes {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(LanfasieBenderson.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Benderson>> BENDERSON = ENTITY_TYPES.registerEntityType("benderson", Benderson::new, MobCategory.MONSTER,
            b -> b.fireImmune().sized(0.6f, 2.375f).immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(12));

    public static final DeferredHolder<EntityType<?>, EntityType<DawnEntity>> DAWN = ENTITY_TYPES.registerEntityType("dawn", DawnEntity::new, MobCategory.CREATURE,
            b -> b.fireImmune().sized(0.6f, 2.375f).immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MundanePraiserBard>> MUNDANE_PRAISER_BARD = ENTITY_TYPES.registerEntityType("mundane_praiser_bard", MundanePraiserBard::new, MobCategory.CREATURE,
            b -> b.fireImmune().sized(0.6f, 2.125f).immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MundanePraiserWhiteMage>> MUNDANE_PRAISER_WHITE_MAGE = ENTITY_TYPES.registerEntityType("mundane_praiser_white_mage", MundanePraiserWhiteMage::new, MobCategory.CREATURE,
            b -> b.fireImmune().sized(0.6f, 2.125f).immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<MundanePraiserRedMage>> MUNDANE_PRAISER_RED_MAGE = ENTITY_TYPES.registerEntityType("mundane_praiser_red_mage", MundanePraiserRedMage::new, MobCategory.CREATURE,
            b -> b.fireImmune().sized(0.6f, 2.125f).immuneTo(Blocks.WITHER_ROSE).clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<TargetMarker>> TARGET_MARKER = ENTITY_TYPES.registerEntityType("target_marker", TargetMarker::new, MobCategory.MISC,
            b -> b.sized(0, 0).noLootTable().clientTrackingRange(16));

    public static final DeferredHolder<EntityType<?>, EntityType<Anticalabrum>> ANTICALABRUM = ENTITY_TYPES.registerEntityType("anticalabrum", Anticalabrum::new, MobCategory.MISC,
            b -> b.sized(0.25f, 2f).noLootTable().clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<DelayedAttackMarker>> DELAYED_ATTACK_MARKER = ENTITY_TYPES.registerEntityType("delayed_attack_marker", DelayedAttackMarker::new, MobCategory.MISC,
            b -> b.sized(0, 0).noLootTable().clientTrackingRange(12));

    public static final DeferredHolder<EntityType<?>, EntityType<UnforgivenSpoiling>> UNFORGIVEN_SPOILING = ENTITY_TYPES.registerEntityType("unforgiven_spoiling", UnforgivenSpoiling::new, MobCategory.MONSTER,
            b -> b.sized(0.8f, 1.5f).eyeHeight(1.2f).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<UnforgivenRidicule>> UNFORGIVEN_RIDICULE = ENTITY_TYPES.registerEntityType("unforgiven_ridicule", UnforgivenRidicule::new, MobCategory.MONSTER,
            b -> b.sized(0.6f, 1.8f).eyeHeight(0.6f).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<UnforgivenPerfidy>> UNFORGIVEN_PERFIDY = ENTITY_TYPES.registerEntityType("unforgiven_perfidy", UnforgivenPerfidy::new, MobCategory.MONSTER,
            b -> b.sized(0.5f, 1.6f).eyeHeight(1.45f).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<UnforgivenCowardice>> UNFORGIVEN_COWARDICE = ENTITY_TYPES.registerEntityType("unforgiven_cowardice", UnforgivenCowardice::new, MobCategory.MONSTER,
            b -> b.sized(0.65f, 0.625f).clientTrackingRange(8));
    public static final DeferredHolder<EntityType<?>, EntityType<UnforgivenIndiscretion>> UNFORGIVEN_INDISCRETION = ENTITY_TYPES.registerEntityType("unforgiven_indiscretion", UnforgivenIndiscretion::new, MobCategory.MONSTER,
            b -> b.sized(0.6f, 2.8f).eyeHeight(2.5f).clientTrackingRange(8));

    public static void register(IEventBus modBus){
        ENTITY_TYPES.register(modBus);
        modBus.addListener(AllEntityTypes::createDefaultAttributes);
    }

    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(BENDERSON.get(), Benderson.createAttributes().build());
        event.put(DAWN.get(), DawnEntity.createAttributes().build());
        event.put(MUNDANE_PRAISER_BARD.get(), MundanePraiserBard.createAttributes().build());
        event.put(MUNDANE_PRAISER_WHITE_MAGE.get(), MundanePraiserWhiteMage.createAttributes().build());
        event.put(MUNDANE_PRAISER_RED_MAGE.get(), MundanePraiserRedMage.createAttributes().build());
        event.put(UNFORGIVEN_SPOILING.get(), UnforgivenSpoiling.createAttributes());
        event.put(UNFORGIVEN_RIDICULE.get(), UnforgivenRidicule.createAttributes());
        event.put(UNFORGIVEN_PERFIDY.get(), UnforgivenPerfidy.createAttributes());
        event.put(UNFORGIVEN_COWARDICE.get(), UnforgivenCowardice.createAttributes());
        event.put(UNFORGIVEN_INDISCRETION.get(), UnforgivenIndiscretion.createAttributes());
    }
}
