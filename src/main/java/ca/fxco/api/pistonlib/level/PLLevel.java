package ca.fxco.api.pistonlib.level;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface collects all custom behavior that is injected into {@code Level}s.
 * It provides dummy implementations for all custom behavior. The actual implementations
 * of these methods are provided in their respective Mixin classes.
 */
public interface PLLevel extends LevelAdditions, LevelMBE, LevelPistonInteraction, LevelQuasiPower {


    // LevelAdditions

    @Override
    default void pl$addBlockEntityPostLoad(BlockEntity blockEntity) {
    }


    // LevelMBE

    @Override
    default void pl$prepareBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    @Override
    default BlockEntity pl$getBlockEntityForPlacement(BlockPos pos, BlockState state) {
        return null;
    }


    // LevelPistonInteraction

    @Override
    default void pl$addPistonEvent(BasicPistonBaseBlock pistonBase, BlockPos pos, Direction dir, boolean extend) {
    }


    // LevelQuasiPower

    @Override
    default int pl$getDirectQuasiSignalTo(BlockPos pos, int dist) {
        return 0;
    }

    @Override
    default boolean pl$hasDirectQuasiSignalTo(BlockPos pos, int dist) {
        return false;
    }

    @Override
    default int pl$getStrongestQuasiNeighborSignal(BlockPos pos, int dist) {
        return 0;
    }

    @Override
    default int pl$getStrongestQuasiNeighborSignal(BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    default boolean pl$hasQuasiNeighborSignal(BlockPos pos, int dist) {
        return false;
    }

    @Override
    default boolean pl$hasQuasiNeighborSignal(BlockPos pos, Direction dir, int dist) {
        return false;
    }

    @Override
    default boolean pl$hasQuasiSignal(BlockPos pos, Direction dir, int dist) {
        return false;
    }

    @Override
    default int pl$getQuasiSignal(BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    default int pl$getDirectQuasiSignal(BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    default boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, int dist) {
        return false;
    }

    @Override
    default boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, Direction dir, int dist) {
        return false;
    }

    @Override
    default boolean pl$hasQuasiNeighborSignalBubble(BlockPos pos) {
        return false;
    }
}
