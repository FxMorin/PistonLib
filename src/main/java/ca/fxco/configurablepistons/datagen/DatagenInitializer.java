package ca.fxco.configurablepistons.datagen;

import ca.fxco.configurablepistons.base.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class DatagenInitializer implements DataGeneratorEntrypoint {

    public static List<Block> datagenBlockList = new ArrayList<>();

    // TODO: Add every single block tag to the datagen, don't do anything manually
    // Missing:
    //  - configurable-pistons.tags.block.moving_pistons
    //  - configurable-pistons.tags.block.pistons
    //  - configurable-pistons.tags.block.sticky_blocks
    //  - configurable-pistons.tags.block.unpushable
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ModBlocks.BASIC_PISTON.getName(); // Crucial, makes sure that mod blocks are loaded before doing datagen
        fabricDataGenerator.addProvider(new PistonBlockTagProvider(fabricDataGenerator));
        fabricDataGenerator.addProvider(new PistonModelProvider(fabricDataGenerator));
    }
}
