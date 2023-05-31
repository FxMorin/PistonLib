package ca.fxco.api.pistonlib.block.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

// Adds the ability to change your power level based on if its quasi powering
public interface BlockStateQuasiPower {

    int pl$getQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist);

    int pl$getDirectQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist);

    boolean pl$isQuasiConductor(BlockGetter level, BlockPos pos);

}
