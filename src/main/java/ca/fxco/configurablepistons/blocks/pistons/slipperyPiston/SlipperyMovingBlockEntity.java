package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SlipperyMovingBlockEntity extends BasicMovingBlockEntity {

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY, pos, state, ModBlocks.SLIPPERY_MOVING_BLOCK);
    }

    public SlipperyMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, SlipperyMovingBlock movingBlock) {
        super(type, pos, state, movingBlock);
    }

    public SlipperyMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing,
                                     boolean extending, boolean isSourcePiston) {
        this(ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY, pos, state, movedState, facing, extending, isSourcePiston, ModBlocks.SLIPPERY_MOVING_BLOCK);
    }

    public SlipperyMovingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockState movedState,
                                     Direction facing, boolean extending, boolean isSourcePiston, SlipperyMovingBlock movingBlock) {
        super(type, pos, state, movedState, facing, extending, isSourcePiston, movingBlock);
    }
}
