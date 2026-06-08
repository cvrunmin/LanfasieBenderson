package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllEntityTypes;
import io.github.cvrunmin.lanfasie.benderson.index.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class MyEntityTypeTagsProvider extends KeyTagProvider<EntityType<?>> {
    public MyEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, LanfasieBenderson.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(AllEntityTypes.BENDERSON.getKey(), AllEntityTypes.DAWN.getKey());
        tag(EntityTypeTags.CANNOT_BE_PUSHED_ONTO_BOATS).add(AllEntityTypes.BENDERSON.getKey(), AllEntityTypes.DAWN.getKey());
        tag(AllTags.IMMUNE_BENDERSON_WIPE_ARENA).add(AllEntityTypes.BENDERSON.getKey(), AllEntityTypes.DAWN.getKey());
    }
}
