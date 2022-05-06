package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class DatagenInitializer implements DataGeneratorEntrypoint {

    public static List<Block> datagenBlockList = new ArrayList<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ModBlocks.BASIC_PISTON.getName(); // Crucial, makes sure that mod blocks are loaded before doing datagen
        fabricDataGenerator.addProvider(new PistonBlockTagProvider(fabricDataGenerator));
        fabricDataGenerator.addProvider(new PistonModelProvider(fabricDataGenerator));
    }
}
