package ca.fxco.configurablepistons.blocks.pistons.speedPiston;

import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedMovingBlock extends BasicMovingBlock {

    private final float speed;

    public SpeedMovingBlock(float speed) {
        super();
        this.speed = speed;
    }

    @Override
    public BlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity,
                                            Direction facing, boolean extending, boolean isSourcePiston) {
        return new SpeedMovingBlockEntity(this.speed, pos, state, movedState, facing, extending, isSourcePiston);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY);
    }
}
