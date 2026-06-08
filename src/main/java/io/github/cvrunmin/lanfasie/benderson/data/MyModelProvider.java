package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.equipment.ShallowayShieldSpecialRenderer;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.List;
import java.util.Optional;

public class MyModelProvider extends ModelProvider {
    public MyModelProvider(PackOutput output) {
        super(output, LanfasieBenderson.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(AllBlocks.DEEP_LATENT_CALLER.get(),
                BlockModelGenerators.plainVariant(new TexturedModel(TextureMapping.cube(new Material(Identifier.withDefaultNamespace("block/sculk_shrieker_inner_top"))), ModelTemplates.CUBE_ALL).create(AllBlocks.DEEP_LATENT_CALLER.get(), blockModels.modelOutput))));
        itemModels.generateFlatItem(AllItems.AGGRO_UP_ICON.get(), ModelTemplates.FLAT_ITEM);
        {
            ExtendedModelTemplate template = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ITEM)
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, 90, -25).scale(1.02f, 1.02f, 0.68f).translation(1.695f, 4.8f, 1.13f))
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, -90, 25).scale(1.02f, 1.02f, 0.68f).translation(1.695f, 4.8f, 1.13f))
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, 90, -55).scale(1.7f, 1.7f, 0.85f).translation(0, 12.0f, 0.5f))
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, -90, 55).scale(1.7f, 1.7f, 0.85f).translation(0, 12.0f, 0.5f))
                    .build();
            itemModels.generateFlatItem(AllItems.SWORD_OF_DAWNWAITER.get(), template);
            itemModels.generateFlatItem(AllItems.SWORD_OF_DAWNWAITER_TAINTED.get(), template);
        }
        itemModels.generateFlatItem(AllItems.OMINOUS_ORB.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.UNFORGIVEN_COWARDICE_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.UNFORGIVEN_SPOILING_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.UNFORGIVEN_INDISCRETION_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.UNFORGIVEN_PERFIDY_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.UNFORGIVEN_RIDICULE_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.itemModelOutput.accept(AllItems.PROVOKING_STICK.get(), new ConditionalItemModel.Unbaked(
                Optional.empty(),
                new IsProvokingModelProperty(),
                new CuboidItemModelWrapper.Unbaked(ModelTemplates.FLAT_HANDHELD_ITEM.create(
                        Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "item/provoking_stick_on"),
                        new TextureMapping().put(TextureSlot.LAYER0, new Material(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "item/revoking_stick"))),
                        itemModels.modelOutput
                        ), Optional.empty(), List.of()),
                new CuboidItemModelWrapper.Unbaked(ModelTemplates.FLAT_HANDHELD_ITEM.create(
                        Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "item/provoking_stick"),
                        new TextureMapping().put(TextureSlot.LAYER0, new Material(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "item/provoking_stick"))),
                        itemModels.modelOutput
                        ), Optional.empty(), List.of())
        ));
        itemModels.generateFlatItem(AllItems.SHALLOWAY_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        {
            var item = AllItems.SHALLOWAY_SHIELD.get();
            ItemModel.Unbaked normal = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(item), new ShallowayShieldSpecialRenderer.Unbaked());
            ItemModel.Unbaked blocking = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(item, "_blocking"), new ShallowayShieldSpecialRenderer.Unbaked());
            itemModels.itemModelOutput
                    .accept(item, ItemModelUtils.conditional(ShieldSpecialRenderer.DEFAULT_TRANSFORMATION, ItemModelUtils.isUsingItem(), blocking, normal));
        }
    }

}
