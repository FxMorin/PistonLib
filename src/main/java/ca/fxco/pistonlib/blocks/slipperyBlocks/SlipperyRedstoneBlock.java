package ca.fxco.pistonlib.blocks.slipperyBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

public class SlipperyRedstoneBlock extends BaseSlipperyBlock {

    public SlipperyRedstoneBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState neighborState, Direction dir) {
        return neighborState.is(this) || super.skipRendering(state, neighborState, dir);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return Redstone.SIGNAL_MAX;
    }
}
