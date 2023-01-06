package ca.fxco.configurablepistons.datagen;

import java.util.function.Consumer;

import org.slf4j.Logger;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;

public class ModRecipeProvider extends FabricRecipeProvider {

    public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> exporter) {
		LOGGER.info("Generating recipes...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating recipes for piston family "+family.getId()+"...");

			Block normalBase = family.getBaseBlock(PistonType.DEFAULT);
			Block stickyBase = family.getBaseBlock(PistonType.STICKY);

			if(normalBase != null && stickyBase != null && normalBase.asItem() != Items.AIR && stickyBase.asItem() != Items.AIR) {
				offerStickyPistonRecipe(exporter, stickyBase, normalBase);
			}
		}

		LOGGER.info("Finished generating recipes for pistons, generating for other items...");

		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_SLIME_BLOCK, Blocks.SLIME_BLOCK);
		offerSlipperyBlockRecipe(exporter, ModBlocks.SLIPPERY_STONE_BLOCK, Blocks.STONE);

		LOGGER.info("Finished generating recipes!");
	}

	public void offerSlipperyBlockRecipe(Consumer<FinishedRecipe> exporter, Block slipperyBlock, Block baseBlock) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, slipperyBlock, 1).requires(baseBlock).requires(Items.POTION).unlockedBy(getHasName(baseBlock), has(baseBlock)).save(exporter);
	}

	public void offerStickyPistonRecipe(Consumer<FinishedRecipe> exporter, Block stickyPiston, Block regularPiston) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, stickyPiston).define('P', regularPiston).define('S', Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(exporter);
	}
}
