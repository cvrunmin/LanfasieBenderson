package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

public class AllTags {
    public static final TagKey<EntityType<?>> IMMUNE_BENDERSON_WIPE_ARENA = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "immune_benderson_wipe_arena"));
    public static final TagKey<EntityType<?>> IGNORE_UNFORGIVEN_INDISCRETION_BROADCAST = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "ignore_unforgiven_indiscretion_broadcast"));
    public static final TagKey<EntityType<?>> SIN_BEARER = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "sin_bearer"));
    public static final TagKey<EntityType<?>> CAN_GET_PROVOKED = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "can_get_provoked"));

    public static final TagKey<Biome> PREFERRED_PLACE_TO_SPAWN_UNFORGIVEN_MONSTER = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "preferred_place_to_spawn_unforgiven_monster"));
}
