package ca.fxco.pistonlib.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

// This should be part of an open API unlike ILevel, we need to split them and make it easier for people to use.
// preferable before making the API public. Also should rename this interface xD
public interface QLevel {

    int getDirectQuasiSignalTo(BlockPos pos, int dist);

    boolean hasDirectQuasiSignalTo(BlockPos pos, int d);

    int getStrongestQuasiNeighborSignal(BlockPos blockPos, int dist);

    boolean hasQuasiNeighborSignal(BlockPos blockPos, int dist);

    boolean hasQuasiSignal(BlockPos blockPos, Direction direction, int dist);

    int getQuasiSignal(BlockPos blockPos, Direction direction, int dist);

    int getDirectQuasiSignal(BlockPos pos, Direction dir, int dist);

    boolean hasQuasiNeighborSignalColumn(BlockPos blockPos, int dist);

    boolean hasQuasiNeighborSignalColumn(BlockPos blockPos, int dist, boolean bothDirections);

    boolean hasQuasiNeighborSignalBubble(BlockPos pos);
}
