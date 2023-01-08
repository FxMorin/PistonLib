package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StickyMovingBlockEntity extends BasicMovingBlockEntity {

    public StickyMovingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.STICKY_MOVING_BLOCK_ENTITY, pos, state, ModBlocks.STICKY_MOVING_BLOCK);
    }

    public StickyMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, StickyMovingBlock movingBlock) {
        super(type, pos, state, movingBlock);
    }

    public StickyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                   boolean extending, boolean isSourcePiston) {
        this(ModBlockEntities.STICKY_MOVING_BLOCK_ENTITY, pos, state, movedState, facing, extending, isSourcePiston,
            ModBlocks.STICKY_MOVING_BLOCK);
    }
    public StickyMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockState movedState,
                                   Direction facing, boolean extending, boolean isSourcePiston, StickyMovingBlock movingBlock) {
        super(type, pos, state, movedState, facing, extending, isSourcePiston, movingBlock);
    }
}
