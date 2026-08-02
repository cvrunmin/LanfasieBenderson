package io.github.cvrunmin.lanfasie.benderson.data;

import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class MyRecipeProvider extends RecipeProvider {
    protected MyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.END_GUARDIAN_STATUE)
                .pattern(" X ")
                .pattern(" O ")
                .pattern("ZZZ")
                .define('X', Blocks.STONE_BRICKS)
                .define('O', AllItems.ECHO_OF_ENDER)
                .define('Z', Blocks.POLISHED_ANDESITE_SLAB)
                .unlockedBy("has_echo_of_ender", this.has(AllItems.ECHO_OF_ENDER))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.FELIS_INVISIBILIS_STATUE)
                .pattern(" X ")
                .pattern(" O ")
                .pattern("ZZZ")
                .define('X', Blocks.STONE_BRICKS)
                .define('O', AllItems.ECHO_OF_FELIS)
                .define('Z', Blocks.POLISHED_ANDESITE_SLAB)
                .unlockedBy("has_echo_of_felis", this.has(AllItems.ECHO_OF_FELIS))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.NETHER_DOG_STATUE)
                .pattern(" X ")
                .pattern(" O ")
                .pattern("ZZZ")
                .define('X', Blocks.STONE_BRICKS)
                .define('O', AllItems.ECHO_OF_METHYL_ORANGE)
                .define('Z', Blocks.POLISHED_ANDESITE_SLAB)
                .unlockedBy("has_echo_of_methyl_orange", this.has(AllItems.ECHO_OF_METHYL_ORANGE))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.HYDRO_DREAMER_STATUE)
                .pattern(" X ")
                .pattern(" O ")
                .pattern("ZZZ")
                .define('X', Blocks.STONE_BRICKS)
                .define('O', AllItems.ECHO_OF_HYDROUS)
                .define('Z', Blocks.POLISHED_ANDESITE_SLAB)
                .unlockedBy("has_echo_of_hydrous", this.has(AllItems.ECHO_OF_HYDROUS))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.VOID_HARE_STATUE)
                .pattern(" X ")
                .pattern(" O ")
                .pattern("ZZZ")
                .define('X', Blocks.STONE_BRICKS)
                .define('O', AllItems.ECHO_OF_VOID_HARE)
                .define('Z', Blocks.POLISHED_ANDESITE_SLAB)
                .unlockedBy("has_echo_of_void_hare", this.has(AllItems.ECHO_OF_VOID_HARE))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.PROVOKING_STICK)
                .pattern("  S")
                .pattern(" SR")
                .pattern("S W")
                .define('S', Items.STICK)
                .define('R', Blocks.RED_WOOL)
                .define('W', Blocks.WHITE_WOOL)
                .unlockedBy("has_wools", this.has(ItemTags.WOOL))
                .save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, AllItems.PROVOKING_CLOTH)
                .pattern("SSS")
                .pattern(" RR")
                .pattern(" RR")
                .define('S', Items.STICK)
                .define('R', Blocks.RED_WOOL)
                .unlockedBy("has_wools", this.has(ItemTags.WOOL))
                .save(this.output);
    }

    public static class RecipeProviderRunner extends RecipeProvider.Runner{

        public RecipeProviderRunner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new MyRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Recipe Provider for lanfasie_benderson";
        }
    }
}
