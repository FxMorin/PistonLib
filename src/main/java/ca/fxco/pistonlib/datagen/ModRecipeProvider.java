package ca.fxco.pistonlib.datagen;

import java.util.Map;
import java.util.function.Consumer;

import ca.fxco.api.pistonlib.recipes.pistonCrushing.SingleCrushingConditionalRecipe;
import ca.fxco.api.pistonlib.recipes.pistonCrushing.builders.PairCrushingRecipeBuilder;
import ca.fxco.api.pistonlib.recipes.pistonCrushing.builders.SingleCrushingRecipeBuilder;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModRegistries;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;

public class ModRecipeProvider extends FabricRecipeProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		LOGGER.info("Generating recipes...");

		for (Map.Entry<ResourceKey<PistonFamily>, PistonFamily> entry : ModRegistries.PISTON_FAMILY.entrySet()) {
            ResourceKey<PistonFamily> key = entry.getKey();
            PistonFamily family = entry.getValue();

            LOGGER.info("Generating recipes for piston family "+key.location()+"...");

            Block normalBase = family.getBase(PistonType.DEFAULT);
            Block stickyBase = family.getBase(PistonType.STICKY);

            if(normalBase != null && stickyBase != null && normalBase.asItem() != Items.AIR && stickyBase.asItem() != Items.AIR) {
                offerStickyPistonRecipe(exporter, stickyBase, normalBase);
            }
        }

		LOGGER.info("Finished generating recipes for pistons, generating for other items...");

		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_SLIME_BLOCK, Blocks.SLIME_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_STONE_BLOCK, Blocks.STONE);

		generateRecipes(exporter, new BlockFamily.Builder(Blocks.OBSIDIAN).slab(ModBlocks.OBSIDIAN_SLAB_BLOCK).stairs(ModBlocks.OBSIDIAN_STAIR_BLOCK).getFamily());

		SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.IRON_ORE), Items.RAW_IRON).save(exporter);
		SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.COPPER_ORE), Items.RAW_COPPER).save(exporter);
		SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.GOLD_ORE), Items.RAW_GOLD).save(exporter);

		//SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.STONE_BRICKS), Items.CRACKED_STONE_BRICKS).mustBeAgainst(Blocks.OBSIDIAN).save(exporter);
		SingleCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.STONE_BRICKS), Items.CRACKED_STONE_BRICKS)
				.hasConditional(SingleCrushingConditionalRecipe.Condition.HIGHER_RESISTANCE, 1199F).save(exporter);
		//offerCrushingCrackedRecipe(exporter, Blocks.STONE_BRICKS, Items.CRACKED_STONE_BRICKS);

		//PairCrushingRecipeBuilder.crushing(Ingredient.of(Blocks.OAK_PLANKS), Ingredient.of(Blocks.OAK_PLANKS), Items.STICK).save(exporter);

		LOGGER.info("Finished generating recipes!");
	}

	public void offerSlipperyBlockRecipe(Consumer<FinishedRecipe> exporter, Block slipperyBlock, Block baseBlock) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, slipperyBlock, 1).requires(baseBlock).requires(Items.POTION).unlockedBy(getHasName(baseBlock), has(baseBlock)).save(exporter);
	}

	public void offerStickyPistonRecipe(Consumer<FinishedRecipe> exporter, Block stickyPiston, Block regularPiston) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, stickyPiston).define('P', regularPiston).define('S', Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(exporter);
	}

	public void offerCrushingCrackedRecipe(Consumer<FinishedRecipe> exporter, Block block, Item item) {
		SingleCrushingRecipeBuilder.crushing(Ingredient.of(block), item)
				.hasConditional(SingleCrushingConditionalRecipe.Condition.HIGHER_RESISTANCE, 1199F).save(exporter);
	}
}
