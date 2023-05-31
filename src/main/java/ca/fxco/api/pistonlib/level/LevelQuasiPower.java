package ca.fxco.api.pistonlib.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface LevelQuasiPower {

    int pl$getDirectQuasiSignalTo(BlockPos pos, int dist);

    boolean pl$hasDirectQuasiSignalTo(BlockPos pos, int dist);

    int pl$getStrongestQuasiNeighborSignal(BlockPos pos, int dist);

    int pl$getStrongestQuasiNeighborSignal(BlockPos pos, Direction dir, int dist);

    boolean pl$hasQuasiNeighborSignal(BlockPos pos, int dist);

    boolean pl$hasQuasiNeighborSignal(BlockPos pos, Direction dir, int dist);

    boolean pl$hasQuasiSignal(BlockPos pos, Direction dir, int dist);

    int pl$getQuasiSignal(BlockPos pos, Direction dir, int dist);

    int pl$getDirectQuasiSignal(BlockPos pos, Direction dir, int dist);

    boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, int dist);

    boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, Direction dir, int dist);

    boolean pl$hasQuasiNeighborSignalBubble(BlockPos pos);

}
