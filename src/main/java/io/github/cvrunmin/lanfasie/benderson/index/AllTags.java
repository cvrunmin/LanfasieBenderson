package io.github.cvrunmin.lanfasie.benderson.index;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AllTags {
    public static final TagKey<EntityType<?>> IMMUNE_BENDERSON_WIPE_ARENA = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "immune_benderson_wipe_arena"));
}
