package ca.fxco.configurablepistons.datagen;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.PistonType;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		LOGGER.info("Generating block tags...");

		FabricTagBuilder pistonsTag = getOrCreateTagBuilder(ModTags.PISTONS).add(Blocks.PISTON, Blocks.STICKY_PISTON);
		FabricTagBuilder movingPistonsTag = getOrCreateTagBuilder(ModTags.MOVING_PISTONS).add(Blocks.MOVING_PISTON);

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			Block normalBase = family.getBaseBlock(PistonType.DEFAULT);
			Block stickyBase = family.getBaseBlock(PistonType.STICKY);
			Block movingBlock = family.getMovingBlock();

			if(normalBase != null) pistonsTag.add(normalBase);
			if(stickyBase != null) pistonsTag.add(stickyBase);
			if(movingBlock != null) movingPistonsTag.add(movingBlock);
		}

		getOrCreateTagBuilder(ModTags.SLIPPERY_BLOCKS).add(ModBlocks.SLIPPERY_SLIME_BLOCK, ModBlocks.SLIPPERY_REDSTONE_BLOCK, ModBlocks.SLIPPERY_STONE_BLOCK, ModBlocks.SLIPPERY_PISTON_HEAD, ModBlocks.SLIPPERY_MOVING_BLOCK);
		getOrCreateTagBuilder(ModTags.SLIPPERY_IGNORE_BLOCKS).add(Blocks.OBSERVER, Blocks.REDSTONE_BLOCK).addTag(ModTags.PISTONS).addTag(ModTags.MOVING_PISTONS);
		getOrCreateTagBuilder(ModTags.SLIPPERY_TRANSPARENT_BLOCKS).add(ModBlocks.SLIPPERY_PISTON, ModBlocks.SLIPPERY_STICKY_PISTON);
		getOrCreateTagBuilder(ModTags.UNPUSHABLE).add(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR);

		getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE).addTag(ModTags.MOVING_PISTONS);
		getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE).addTag(ModTags.MOVING_PISTONS);

		LOGGER.info("Finished generating block tags!");
	}
}
