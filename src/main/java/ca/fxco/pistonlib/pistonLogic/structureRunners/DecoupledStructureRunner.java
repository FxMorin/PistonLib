package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.MergingPistonStructureResolver;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Structure Runner Wrapper that decouples the structure runner from having a physical piston
 * Allowing the Piston pushing code to be used anywhere**
 */
@RequiredArgsConstructor
public class DecoupledStructureRunner implements StructureRunner {

    private final StructureRunner structureRunner;

    @Override
    public PistonFamily getFamily() {
        return structureRunner.getFamily();
    }

    @Override
    public void taskRemovePistonHeadOnRetract(Level level, BlockPos pos, Direction facing, boolean extend) {
        // No piston head to modify!
    }

    @Override
    public void taskSetPositionsToMove(Level level, List<BlockPos> toMove, Direction moveDir) {
        structureRunner.taskSetPositionsToMove(level, toMove, moveDir);
    }

    @Override
    public void taskMergeBlocks(Level level, BlockPos pos, Direction facing, boolean extend, MergingPistonStructureResolver structure, Direction moveDir) {
        structureRunner.taskMergeBlocks(level, pos, facing, extend, structure, moveDir);
    }

    @Override
    public void taskDestroyBlocks(Level level, BlockPos pos, List<BlockPos> toDestroy, BlockState[] affectedStates, AtomicInteger affectedIndex) {
        structureRunner.taskDestroyBlocks(level, pos, toDestroy, affectedStates, affectedIndex);
    }

    @Override
    public void taskMoveBlocks(Level level, BlockPos pos, PistonStructureResolver structure, Direction facing, boolean extend, List<BlockPos> toMove, BlockState[] affectedStates, AtomicInteger affectedIndex, Direction moveDir) {
        structureRunner.taskMoveBlocks(level, pos, structure, facing, extend, toMove, affectedStates, affectedIndex, moveDir);
    }

    @Override
    public void taskPlaceExtendingHead(Level level, BlockPos pos, Direction facing, boolean extend) {
        // Don't place a piston head without a piston base!
    }

    @Override
    public void taskRemoveLeftOverBlocks(Level level) {
        structureRunner.taskRemoveLeftOverBlocks(level);
    }

    @Override
    public void taskDoRemoveNeighborUpdates(Level level) {
        structureRunner.taskDoRemoveNeighborUpdates(level);
    }

    @Override
    public void taskDoDestroyNeighborUpdates(Level level, List<BlockPos> toDestroy, BlockState[] affectedStates, AtomicInteger affectedIndex) {
        structureRunner.taskDoDestroyNeighborUpdates(level, toDestroy, affectedStates, affectedIndex);
    }

    @Override
    public void taskDoMoveNeighborUpdates(Level level, List<BlockPos> toMove, BlockState[] affectedStates, AtomicInteger affectedIndex) {
        structureRunner.taskDoMoveNeighborUpdates(level, toMove, affectedStates, affectedIndex);
    }

    @Override
    public void taskDoUnMergeUpdates(Level level) {
        structureRunner.taskDoUnMergeUpdates(level);
    }
}
