package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

public class MyDamageTypeTagsProvider extends KeyTagProvider<DamageType> {
    public MyDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, LanfasieBenderson.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(AllDamageTypes.LETHAL_ATTACK);
        this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(AllDamageTypes.LETHAL_ATTACK);
        this.tag(DamageTypeTags.NO_KNOCKBACK).add(AllDamageTypes.LETHAL_ATTACK, AllDamageTypes.BOSS_ABILITY_ATTACK);
        this.tag(DamageTypeTags.BYPASSES_SHIELD).add(AllDamageTypes.BOSS_ABILITY_ATTACK);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(AllDamageTypes.BOSS_ABILITY_ATTACK);
    }
}
