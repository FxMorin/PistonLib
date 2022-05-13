package ca.fxco.configurablepistons.base;

import net.minecraft.state.property.IntProperty;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock.MAX_DISTANCE;

public class ModProperties {
    public static final IntProperty SLIPPERY_DISTANCE = IntProperty.of("distance", 0, MAX_DISTANCE);
}
