package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModProperties {

    public static final IntegerProperty SLIPPERY_DISTANCE = IntegerProperty.create("distance", 0, BaseSlipperyBlock.MAX_DISTANCE);

}
