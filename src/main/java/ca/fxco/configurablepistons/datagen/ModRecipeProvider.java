package ca.fxco.configurablepistons.datagen;

import java.util.function.Consumer;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import org.slf4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

public class ModRecipeProvider extends FabricRecipeProvider {
	public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		LOGGER.info("Generating recipes...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating recipes for piston family "+family.getId()+"...");

			Block piston = family.getPistonBlock();
			Block stickyPiston = family.getStickyPistonBlock();

			if(piston != null && stickyPiston != null && piston.asItem() != Items.AIR && stickyPiston.asItem() != Items.AIR) {
				offerStickyPistonRecipe(exporter, stickyPiston, piston);
			}
		}

		LOGGER.info("Finished generating recipes for pistons, generating for other items...");

		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_SLIME_BLOCK, Blocks.SLIME_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_STONE_BLOCK, Blocks.STONE);

		LOGGER.info("Finished generating recipes!");
	}

	public void offerSlipperyBlockRecipe(Consumer<RecipeJsonProvider> exporter, Block slipperyBlock, Block baseBlock) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, slipperyBlock, 1).input(baseBlock).input(Items.POTION).criterion(hasItem(baseBlock), conditionsFromItem(baseBlock)).offerTo(exporter);
	}

	public void offerStickyPistonRecipe(Consumer<RecipeJsonProvider> exporter, Block stickyPiston, Block regularPiston) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, stickyPiston).input('P', regularPiston).input('S', Items.SLIME_BALL).pattern("S").pattern("P").criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL)).offerTo(exporter);
	}
}
