package ca.fxco.configurablepistons.datagen;

import org.slf4j.Logger;

import ca.fxco.configurablepistons.ConfigurablePistons;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ConfigurablePistonsDatagen implements DataGeneratorEntrypoint {

    public static final Logger LOGGER = ConfigurablePistons.LOGGER;

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		LOGGER.info("Starting Configurable Pistons datagen...");

		FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
	}
}
