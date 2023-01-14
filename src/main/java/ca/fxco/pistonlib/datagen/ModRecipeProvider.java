package ca.fxco.pistonlib.datagen;

import java.util.function.Consumer;

import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamilies;
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

		generateRecipes(exporter, new BlockFamily.Builder(Blocks.OBSIDIAN).slab(ModBlocks.OBSIDIAN_SLAB_BLOCK).stairs(ModBlocks.OBSIDIAN_STAIR_BLOCK).getFamily());

		LOGGER.info("Finished generating recipes!");
	}

	public void offerSlipperyBlockRecipe(Consumer<FinishedRecipe> exporter, Block slipperyBlock, Block baseBlock) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, slipperyBlock, 1).requires(baseBlock).requires(Items.POTION).unlockedBy(getHasName(baseBlock), has(baseBlock)).save(exporter);
	}

	public void offerStickyPistonRecipe(Consumer<FinishedRecipe> exporter, Block stickyPiston, Block regularPiston) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, stickyPiston).define('P', regularPiston).define('S', Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has(Items.SLIME_BALL)).save(exporter);
	}
}
