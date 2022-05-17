package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SlipperyPistonBlockEntity extends BasicPistonBlockEntity {

    public SlipperyPistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.SLIPPERY_PISTON_BLOCK_ENTITY);
    }

    public SlipperyPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                     boolean extending, boolean source, SlipperyPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.SLIPPERY_PISTON_BLOCK_ENTITY);
    }
}
