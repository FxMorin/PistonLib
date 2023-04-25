package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.MergingPistonStructureResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface StructureRunner {

    PistonFamily getFamily();

    void taskRemovePistonHeadOnRetract(Level level, BlockPos pos, Direction facing, boolean extend);

    void taskSetPositionsToMove(Level level, List<BlockPos> toMove, Direction moveDir);

    void taskMergeBlocks(Level level, BlockPos pos, Direction facing, boolean extend,
                         MergingPistonStructureResolver structure, Direction moveDir);

    void taskDestroyBlocks(Level level, BlockPos pos, List<BlockPos> toDestroy,
                           BlockState[] affectedStates, AtomicInteger affectedIndex);

    void taskPreventTntDuping(Level level, BlockPos pos, List<BlockPos> toMove);

    void taskMoveBlocks(Level level, BlockPos pos, PistonStructureResolver structure, Direction facing,
                        boolean extend, List<BlockPos> toMove, BlockState[] affectedStates,
                        AtomicInteger affectedIndex, Direction moveDir);

    void taskPlaceExtendingHead(Level level, BlockPos pos, Direction facing, boolean extend);

    void taskRemoveLeftOverBlocks(Level level);

    void taskDoRemoveNeighborUpdates(Level level);

    void taskDoDestroyNeighborUpdates(Level level, List<BlockPos> toMove, List<BlockPos> toDestroy,
                                      BlockState[] affectedStates, AtomicInteger affectedIndex);

    void taskDoMoveNeighborUpdates(Level level, List<BlockPos> toMove, BlockState[] affectedStates,
                                   AtomicInteger affectedIndex);

    void taskDoUnMergeUpdates(Level level);

    default boolean run(Level level, BlockPos pos, Direction facing, boolean extend,
                        BasicStructureResolver.Factory<? extends BasicStructureResolver> structureProvider) {
        taskRemovePistonHeadOnRetract(level, pos, facing, extend);

        PistonStructureResolver structure = structureProvider.create(level, pos, facing, extend);

        if (!structure.resolve()) {
            return false;
        }

        List<BlockPos> toMove = structure.getToPush();
        List<BlockPos> toDestroy = structure.getToDestroy();

        Direction moveDir = extend ? facing : facing.getOpposite();

        // collect blocks to move
        taskSetPositionsToMove(level, toMove, moveDir);

        if (structure instanceof MergingPistonStructureResolver mergingStructure) {
            taskMergeBlocks(level, pos, facing, extend, mergingStructure, moveDir);
        }

        BlockState[] affectedStates = new BlockState[toMove.size() + toDestroy.size()];
        AtomicInteger affectedIndex = new AtomicInteger();

        // destroy blocks
        taskDestroyBlocks(level, pos, toDestroy, affectedStates, affectedIndex);

        if (PistonLibConfig.tntDupingFix) {
            taskPreventTntDuping(level, pos, toMove);
        }

        // move blocks
        taskMoveBlocks(level, pos, structure, facing, extend, toMove, affectedStates, affectedIndex, moveDir);

        // place extending head
        taskPlaceExtendingHead(level, pos, facing, extend);

        // remove left over blocks
        taskRemoveLeftOverBlocks(level);

        // do remove neighbor updates
        taskDoRemoveNeighborUpdates(level);

        affectedIndex = new AtomicInteger();

        // do destroy neighbor updates
        taskDoDestroyNeighborUpdates(level, toMove, toDestroy, affectedStates, affectedIndex);

        // do move neighbor updates
        taskDoMoveNeighborUpdates(level, toMove, affectedStates, affectedIndex);

        // do unmerge neighbor updates
        taskDoUnMergeUpdates(level);

        if (extend) {
            level.updateNeighborsAt(pos.relative(facing), getFamily().getHead());
        }

        return true;
    }
}
