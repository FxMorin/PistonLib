package ca.fxco.pistonlib.blocks.pistons.slipperyPiston;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SlipperyMovingBlockEntity extends BasicMovingBlockEntity {

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY);
    }

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state, BlockEntityType<?> type) {
        super(pos, state, type);
    }

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                     boolean extending, boolean isSourcePiston) {
        this(pos, state, movedState, facing, extending, isSourcePiston, ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY);
    }

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                     boolean extending, boolean isSourcePiston, BlockEntityType<?> type) {
        super(pos, state, movedState, facing, extending, isSourcePiston, type);
    }
}
