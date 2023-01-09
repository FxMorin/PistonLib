package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StickyMovingBlockEntity extends BasicMovingBlockEntity {

    public StickyMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public StickyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                   boolean extending, boolean isSourcePiston, BasicMovingBlock movingBlock) {
        this(pos, state, movedState, facing, extending, isSourcePiston, movingBlock,
                ModBlockEntities.STICKY_MOVING_BLOCK_ENTITY);
    }

    public StickyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                   boolean extending, boolean isSourcePiston, BasicMovingBlock movingBlock,
                                   BlockEntityType<?> type) {
        super(pos, state, movedState, facing, extending, isSourcePiston, movingBlock, type);
    }
}
