package ca.fxco.api.pistonlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockQuasiPower {

    int pl$getQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist);

    int pl$getDirectQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist);

    boolean pl$isQuasiConductor(BlockState state, BlockGetter level, BlockPos pos);

}
