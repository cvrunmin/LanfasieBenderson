package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.marker.DelayedAttackMarker;
import io.github.cvrunmin.lanfasie.benderson.content.marker.TargetMarker;
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

    public static final DeferredHolder<EntityType<?>, EntityType<TargetMarker>> TARGET_MARKER = ENTITY_TYPES.registerEntityType("target_marker", TargetMarker::new, MobCategory.MISC,
            b -> b.sized(0, 0).noLootTable().clientTrackingRange(16));

    public static final DeferredHolder<EntityType<?>, EntityType<Anticalabrum>> ANTICALABRUM = ENTITY_TYPES.registerEntityType("anticalabrum", Anticalabrum::new, MobCategory.MISC,
            b -> b.sized(0.25f, 2f).noLootTable().clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<DelayedAttackMarker>> DELAYED_ATTACK_MARKER = ENTITY_TYPES.registerEntityType("delayed_attack_marker", DelayedAttackMarker::new, MobCategory.MISC,
            b -> b.sized(0, 0).noLootTable().clientTrackingRange(12));

    public static void register(IEventBus modBus){
        ENTITY_TYPES.register(modBus);
        modBus.addListener(AllEntityTypes::createDefaultAttributes);
    }

    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(BENDERSON.get(), Benderson.createAttributes().build());
    }
}
