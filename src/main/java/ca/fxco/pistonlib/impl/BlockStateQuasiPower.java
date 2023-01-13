package ca.fxco.pistonlib.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

// Adds the ability to change your power level based on if its quasi powering
public interface BlockStateQuasiPower {

    int getQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction, int dist);

    boolean hasQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction, int dist);
}
