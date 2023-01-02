package ca.fxco.configurablepistons.datagen;

import java.util.concurrent.CompletableFuture;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import org.slf4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
		LOGGER.info("Generating block tags...");

		FabricTagBuilder pistonsTag = getOrCreateTagBuilder(ModTags.PISTONS).add(Blocks.PISTON, Blocks.STICKY_PISTON);
		FabricTagBuilder movingPistonsTag = getOrCreateTagBuilder(ModTags.MOVING_PISTONS).add(Blocks.MOVING_PISTON);

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			Block piston = family.getPistonBlock();
			Block stickyPiston = family.getStickyPistonBlock();
			Block movingPiston = family.getExtensionBlock();

			if(piston != null) pistonsTag.add(piston);
			if(stickyPiston != null) pistonsTag.add(stickyPiston);
			if(movingPiston != null) movingPistonsTag.add(movingPiston);
		}

		getOrCreateTagBuilder(ModTags.SLIPPERY_BLOCKS).add(ModBlocks.SLIPPERY_SLIME_BLOCK, ModBlocks.SLIPPERY_REDSTONE_BLOCK, ModBlocks.SLIPPERY_STONE_BLOCK, ModBlocks.SLIPPERY_PISTON_HEAD, ModBlocks.SLIPPERY_MOVING_PISTON);
		getOrCreateTagBuilder(ModTags.SLIPPERY_IGNORE_BLOCKS).add(Blocks.OBSERVER, Blocks.REDSTONE_BLOCK).addTag(ModTags.PISTONS).addTag(ModTags.MOVING_PISTONS);
		getOrCreateTagBuilder(ModTags.SLIPPERY_TRANSPARENT_BLOCKS).add(ModBlocks.SLIPPERY_PISTON, ModBlocks.SLIPPERY_STICKY_PISTON);
		getOrCreateTagBuilder(ModTags.UNPUSHABLE).add(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR);

		getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE).addTag(ModTags.MOVING_PISTONS);
		getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE).addTag(ModTags.MOVING_PISTONS);

		LOGGER.info("Finished generating block tags!");
	}
}
