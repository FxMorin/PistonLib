package ca.fxco.pistonlib.blocks;

import ca.fxco.api.pistonlib.block.BlockQuasiPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class QuasiBlock extends Block implements BlockQuasiPower {

    public QuasiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return false;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return 0;
    }

    @Override
    public int getQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return 15;
    }

    @Override
    public boolean hasQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return true;
    }

    @Override
    public int getDirectQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    public boolean isQuasiConductor(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }
}
