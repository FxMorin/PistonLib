package ca.fxco.api.pistonlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockQuasiPower {

    int getQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist);

    default boolean hasQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return getQuasiSignal(state, blockGetter, pos, dir, dist) > 0;
    }

    int getDirectQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist);

    boolean isQuasiConductor(BlockState state, BlockGetter blockGetter, BlockPos pos);
}
