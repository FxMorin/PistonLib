package ca.fxco.configurablepistons.newBlocks.veryStickyPiston;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StickyPistonBlockEntity extends BasicPistonBlockEntity {

    public StickyPistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.STICKY_PISTON_BLOCK_ENTITY);
    }
    public StickyPistonBlockEntity(BlockPos pos, BlockState state, StickyPistonExtensionBlock extensionBlock) {
        super(pos, state, extensionBlock);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.STICKY_PISTON_BLOCK_ENTITY);
    }
    public StickyPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                 boolean extending, boolean source, StickyPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.STICKY_PISTON_BLOCK_ENTITY);
    }
}
