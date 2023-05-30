package ca.fxco.api.pistonlib.block.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

// Adds the ability to change your power level based on if its quasi powering
public interface BlockStateQuasiPower {

    int getQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction, int dist);

    boolean hasQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction, int dist);

    int getDirectQuasiSignal(BlockGetter blockGetter, BlockPos pos, Direction dir, int dist);

    boolean isQuasiConductor(BlockGetter blockGetter, BlockPos pos);
}
