package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.equipment.ShallowayShieldSpecialRenderer;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
        blockModels.createTrivialCube(AllBlocks.DEEP_LATENT_BLOCK.get());
        blockModels.createTrivialCube(AllBlocks.DEEP_LATENT_CALLER.get());

        {
            MultiVariant lower = BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "block/statue/end_guardian_lower"));
            MultiVariant upper = BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "block/statue/end_guardian_upper"));
            blockModels.blockStateOutput.accept(MultiVariantGenerator
                    .dispatch(AllBlocks.END_GUARDIAN_STATUE.get())
                    .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF)
                            .select(Direction.NORTH, DoubleBlockHalf.LOWER, lower)
                            .select(Direction.EAST, DoubleBlockHalf.LOWER, lower.with(BlockModelGenerators.Y_ROT_90))
                            .select(Direction.SOUTH, DoubleBlockHalf.LOWER, lower.with(BlockModelGenerators.Y_ROT_180))
                            .select(Direction.WEST, DoubleBlockHalf.LOWER, lower.with(BlockModelGenerators.Y_ROT_270))
                            .select(Direction.NORTH, DoubleBlockHalf.UPPER, upper)
                            .select(Direction.EAST, DoubleBlockHalf.UPPER, upper.with(BlockModelGenerators.Y_ROT_90))
                            .select(Direction.SOUTH, DoubleBlockHalf.UPPER, upper.with(BlockModelGenerators.Y_ROT_180))
                            .select(Direction.WEST, DoubleBlockHalf.UPPER, upper.with(BlockModelGenerators.Y_ROT_270))
                    )
            );
        }

        itemModels.generateFlatItem(AllItems.AGGRO_UP_ICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(AllItems.DAWNWAITER_TOTEM.get(), ModelTemplates.FLAT_ITEM);
        {
            ExtendedModelTemplate template = ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ITEM)
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, 90, -25).scale(1.02f, 1.02f, 0.68f).translation(1.695f, 4.8f, 1.13f))
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, -90, 25).scale(1.02f, 1.02f, 0.68f).translation(1.695f, 4.8f, 1.13f))
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, 90, -55).scale(1.7f, 1.7f, 0.85f).translation(0, 12.0f, 0.5f))
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transformVecBuilder -> transformVecBuilder.rotation(0, -90, 55).scale(1.7f, 1.7f, 0.85f).translation(0, 12.0f, 0.5f))
                    .build();
            itemModels.generateFlatItem(AllItems.SWORD_OF_DAWNWAITER.get(), template);
            itemModels.generateFlatItem(AllItems.SWORD_OF_DAWNWAITER_TAINTED.get(), template);
            itemModels.generateFlatItem(AllItems.CLAYMORE_OF_HEI_POWER.get(), template);
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
        generatePraiserBow(itemModels, AllItems.MUNDANE_PRAISER_BOW.get());
        itemModels.generateFlatItem(AllItems.MUNDANE_PRAISER_CANE.get(), ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ROD_ITEM)
                        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, builder -> builder.rotation(-20, 90, 70).translation(3.13f, 2.0f, 0.13f).scale(1.36f, 1.36f, 0.68f))
                        .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, builder -> builder.rotation(-20, -90, -70).translation(3.13f, 2.0f, 0.13f).scale(1.36f, 1.36f, 0.68f))
                        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, builder -> builder.rotation(0, 90, 40).translation(0, -2, 2).scale(1.7f, 1.7f, 0.85f))
                        .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, builder -> builder.rotation(0, -90, -40).translation(0, -2, 2).scale(1.7f, 1.7f, 0.85f))
                .build());
        itemModels.generateFlatItem(AllItems.MUNDANE_PRAISER_RAPIER.get(), ExtendedModelTemplateBuilder.of(ModelTemplates.FLAT_HANDHELD_ITEM)
                        .transform(ItemDisplayContext.GUI, builder -> builder.rotation(180, 180, -90))
                .build());
        itemModels.itemModelOutput.accept(AllItems.MUNDANE_PRAISER_MANA_FOCI.get(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(AllItems.MUNDANE_PRAISER_MANA_FOCI.get())));
    }

    private void generatePraiserBow(ItemModelGenerators itemModels, Item item) {
        var template = ModelTemplates.createItem(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "mundane_praiser_bow").toString(), TextureSlot.LAYER0);
        ItemModel.Unbaked bowModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
        ItemModel.Unbaked pulling0 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_0", template));
        ItemModel.Unbaked pulling1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_1", template));
        ItemModel.Unbaked pulling2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_pulling_2", template));
        itemModels.itemModelOutput
                .accept(
                        item,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new UseDuration(false), 0.05F, pulling0, ItemModelUtils.override(pulling1, 0.65F), ItemModelUtils.override(pulling2, 0.9F)
                                ),
                                bowModel
                        )
                );
    }
}
