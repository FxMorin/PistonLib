package ca.fxco.pistonlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

public class QuasiBlock extends Block {

    public QuasiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return Redstone.SIGNAL_MIN;
    }

    @Override
    public int pl$getQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return Redstone.SIGNAL_MAX;
    }

    @Override
    public int pl$getDirectQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return Redstone.SIGNAL_MIN;
    }

    @Override
    public boolean pl$isQuasiConductor(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
