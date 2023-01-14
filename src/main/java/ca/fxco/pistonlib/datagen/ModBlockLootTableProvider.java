package ca.fxco.pistonlib.datagen;

import java.util.function.BiConsumer;

import org.slf4j.Logger;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.storage.loot.LootTable.Builder;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {

    public static final Logger LOGGER = PistonLib.LOGGER;

	protected ModBlockLootTableProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		LOGGER.info("Generating block loot tables...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating block loot tables for piston family "+family.getId()+"...");
			Block normalBase = family.getBaseBlock(PistonType.DEFAULT);
			Block stickyBase = family.getBaseBlock(PistonType.STICKY);

			if(normalBase != null) dropSelf(normalBase);
			if(stickyBase != null) dropSelf(stickyBase);
		}

		LOGGER.info("Finished generating block loot tables for pistons, generating for other blocks...");

		dropSelf(ModBlocks.HALF_SLIME_BLOCK);
		dropSelf(ModBlocks.HALF_HONEY_BLOCK);
		dropSelf(ModBlocks.HALF_REDSTONE_BLOCK);
		dropSelf(ModBlocks.HALF_OBSIDIAN_BLOCK);
		dropSelf(ModBlocks.HALF_REDSTONE_LAMP_BLOCK);

		dropSelf(ModBlocks.DRAG_BLOCK);
		dropSelf(ModBlocks.STICKYLESS_BLOCK);
		dropSelf(ModBlocks.STICKY_TOP_BLOCK);
		dropSelf(ModBlocks.SLIMY_REDSTONE_BLOCK);
		dropSelf(ModBlocks.ALL_SIDED_OBSERVER);
		dropSelf(ModBlocks.GLUE_BLOCK);
		dropSelf(ModBlocks.POWERED_STICKY_BLOCK);
		dropSelf(ModBlocks.STICKY_CHAIN_BLOCK);
		dropSelf(ModBlocks.AXIS_LOCKED_BLOCK);
		dropSelf(ModBlocks.MOVE_COUNTING_BLOCK);
		dropSelf(ModBlocks.OBSIDIAN_SLAB_BLOCK);
		dropSelf(ModBlocks.OBSIDIAN_STAIR_BLOCK);
		dropSelf(ModBlocks.HEAVY_BLOCK);

		dropSelf(ModBlocks.SLIPPERY_SLIME_BLOCK);
		dropSelf(ModBlocks.SLIPPERY_REDSTONE_BLOCK);
		dropSelf(ModBlocks.SLIPPERY_STONE_BLOCK);

		LOGGER.info("Finished generating block loot tables!");
	}

	// FabricLootTableProvider#accept needs to be overriden because
	// we're using Mojmaps. BlockLootSubProvider#generate is called
	// accept in Yarn and thus would provide that implementation.
    @Override
    public void accept(BiConsumer<ResourceLocation, Builder> t) {
        this.generate(t);
    }
}
