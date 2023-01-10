package ca.fxco.pistonlib.blocks.pistons.longPiston;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static ca.fxco.pistonlib.blocks.pistons.longPiston.LongMovingBlockEntity.MAX_ARM_LENGTH;

public class LongMovingBlock extends BasicMovingBlock {

    public LongMovingBlock() {
        super();
    }

    @Override
    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               @Nullable BlockEntity movedBlockEntity, Direction facing,
                                               boolean extending, boolean isSourcePiston) {
        return createMovingBlockEntity(pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston, MAX_ARM_LENGTH,
            0, false);
    }

    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               BlockEntity movedBlockEntity, Direction facing, boolean extending,
                                               boolean isSourcePiston, int maxLength, int length, boolean isArm) {
        return new LongMovingBlockEntity(pos, state, movedState, facing, extending, isSourcePiston, maxLength, length, isArm);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.LONG_MOVING_BLOCK_ENTITY);
    }
}
