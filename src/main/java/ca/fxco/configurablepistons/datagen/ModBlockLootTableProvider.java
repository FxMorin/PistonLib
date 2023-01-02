package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import org.slf4j.Logger;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
	public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	protected ModBlockLootTableProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		LOGGER.info("Generating block loot tables...");

		for(PistonFamily family : PistonFamilies.getFamilies()) {
			LOGGER.info("Generating block loot tables for piston family "+family.getId()+"...");
			if(family.getPistonBlock() != null) addDrop(family.getPistonBlock());
			if(family.getStickyPistonBlock() != null) addDrop(family.getStickyPistonBlock());
			if(family.getHeadBlock() != null) excludeFromStrictValidation(family.getHeadBlock());
			if(family.getArmBlock() != null) excludeFromStrictValidation(family.getArmBlock());
			if(family.getExtensionBlock() != null) excludeFromStrictValidation(family.getExtensionBlock());
		}

		LOGGER.info("Finished generating block loot tables for pistons, generating for other blocks...");

		addDrop(ModBlocks.HALF_SLIME_BLOCK);
		addDrop(ModBlocks.HALF_HONEY_BLOCK);
		addDrop(ModBlocks.HALF_REDSTONE_BLOCK);
		addDrop(ModBlocks.HALF_OBSIDIAN_BLOCK);

		addDrop(ModBlocks.DRAG_BLOCK);
		addDrop(ModBlocks.STICKYLESS_BLOCK);
		addDrop(ModBlocks.STICKY_TOP_BLOCK);
		addDrop(ModBlocks.SLIMY_REDSTONE_BLOCK);
		addDrop(ModBlocks.ALL_SIDED_OBSERVER);
		addDrop(ModBlocks.GLUE_BLOCK);
		addDrop(ModBlocks.POWERED_STICKY_BLOCK);
		addDrop(ModBlocks.STICKY_CHAIN_BLOCK);

		addDrop(ModBlocks.SLIPPERY_SLIME_BLOCK);
		addDrop(ModBlocks.SLIPPERY_REDSTONE_BLOCK);
		addDrop(ModBlocks.SLIPPERY_STONE_BLOCK);

		LOGGER.info("Finished generating block loot tables!");
	}
}
