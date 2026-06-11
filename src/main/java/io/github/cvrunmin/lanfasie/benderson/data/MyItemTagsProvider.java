package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class MyItemTagsProvider extends KeyTagProvider<Item> {
    public MyItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, LanfasieBenderson.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ItemTags.SWORDS).add(AllItems.SWORD_OF_DAWNWAITER.getKey(), AllItems.SHALLOWAY_SWORD.getKey());
        tag(Tags.Items.MELEE_WEAPON_TOOLS).add(AllItems.SWORD_OF_DAWNWAITER.getKey(), AllItems.SHALLOWAY_SWORD.getKey(), AllItems.MUNDANE_PRAISER_RAPIER.getKey());
        tag(ItemTags.BOW_ENCHANTABLE).add(AllItems.MUNDANE_PRAISER_BOW.getKey());
        tag(Tags.Items.TOOLS_SHIELD).add(AllItems.SHALLOWAY_SHIELD.getKey());
        tag(Tags.Items.TOOLS_BOW).add(AllItems.MUNDANE_PRAISER_BOW.getKey());
    }
}
