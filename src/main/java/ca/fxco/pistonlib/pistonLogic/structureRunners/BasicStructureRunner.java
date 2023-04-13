package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.MergingPistonStructureResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.world.level.block.Block.*;

/**
 * Abstract the entire piston move code into a structure runner
 */
public class BasicStructureRunner {

    protected static final BlockState AIR_STATE = Blocks.AIR.defaultBlockState();

    protected final PistonFamily family;
    protected final PistonType type;

    protected final Map<BlockPos, BlockState> toRemove = new LinkedHashMap<>();
    protected final List<BlockState> statesToMove = new ArrayList<>();
    protected final List<BlockEntity> blockEntitiesToMove = new ArrayList<>();

    public BasicStructureRunner(PistonFamily family, PistonType type) {
        this.family = family;
        this.type = type;
    }

    protected void taskSetPositionsToMove(Level level, List<BlockPos> toMove, Direction moveDir) {
        for (BlockPos posToMove : toMove) {
            BlockState stateToMove = level.getBlockState(posToMove);
            BlockEntity blockEntityToMove = level.getBlockEntity(posToMove);

            if (blockEntityToMove != null) {
                level.removeBlockEntity(posToMove);
                blockEntityToMove.setChanged();
            }

            statesToMove.add(stateToMove);
            blockEntitiesToMove.add(blockEntityToMove);
            toRemove.put(posToMove, stateToMove);
        }
    }

    protected void taskMergeBlocks(Level level, BlockPos pos, Direction facing, boolean extend,
                                   MergingPistonStructureResolver structure, Direction moveDir) {}

    protected void taskDestroyBlocks(Level level, BlockPos pos, List<BlockPos> toDestroy, BlockState[] affectedStates, AtomicInteger affectedIndex) {
        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos posToDestroy = toDestroy.get(i);
            BlockState stateToDestroy = level.getBlockState(posToDestroy);
            BlockEntity blockEntityToDestroy = level.getBlockEntity(posToDestroy);

            dropResources(stateToDestroy, level, posToDestroy, blockEntityToDestroy);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
            if (!stateToDestroy.is(BlockTags.FIRE)) {
                level.addDestroyBlockEffect(posToDestroy, stateToDestroy);
            }

            affectedStates[affectedIndex.getAndIncrement()] = stateToDestroy;
        }
    }

    protected void taskMoveBlocks(Level level, BlockPos pos, PistonStructureResolver structure, Direction facing,
                                  boolean extend, List<BlockPos> toMove, BlockState[] affectedStates,
                                  AtomicInteger affectedIndex, Direction moveDir) {
        int moveSize = toMove.size();
        if (moveSize > 0) {
            StructureGroup structureGroup = null;
            if (moveSize > 1) { // Only use Structure group if there are more than 1 block entities in the group
                structureGroup = StructureGroup.create(level);
            }
            for (int i = moveSize - 1; i >= 0; i--) {
                BlockPos posToMove = toMove.get(i);
                BlockPos dstPos = posToMove.relative(moveDir);
                BlockState stateToMove = statesToMove.get(i);
                BlockEntity blockEntityToMove = blockEntitiesToMove.get(i);

                toRemove.remove(dstPos);

                BlockState movingBlock = this.family.getMoving().defaultBlockState()
                        .setValue(BasicMovingBlock.FACING, facing);
                BasicMovingBlockEntity movingBlockEntity = this.family
                        .newMovingBlockEntity(structureGroup, dstPos, movingBlock, stateToMove, blockEntityToMove, facing, extend, false);

                level.setBlock(dstPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
                level.setBlockEntity(movingBlockEntity);

                affectedStates[affectedIndex.getAndIncrement()] = stateToMove;
            }
        }
    }

    protected void taskPlaceExtendingHead(Level level, BlockPos pos, Direction facing, boolean extend) {
        if (extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = this.family.getHead().defaultBlockState()
                    .setValue(BasicPistonHeadBlock.TYPE, this.type)
                    .setValue(BasicPistonHeadBlock.FACING, facing);

            toRemove.remove(headPos);

            BlockState movingBlock = this.family.getMoving().defaultBlockState()
                    .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = this.family
                    .newMovingBlockEntity(headPos, movingBlock, headState, null, facing, extend, true);

            level.setBlock(headPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);
        }
    }

    protected void taskRemoveLeftOverBlocks(Level level) {
        for (BlockPos posToRemove : toRemove.keySet()) {
            level.setBlock(posToRemove, AIR_STATE, UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
        }
    }

    protected void taskDoRemoveNeighborUpdates(Level level) {
        for (Map.Entry<BlockPos, BlockState> entry : toRemove.entrySet()) {
            BlockPos removedPos = entry.getKey();

            entry.getValue().updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            AIR_STATE.updateNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            AIR_STATE.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
        }
    }

    protected void taskDoDestroyNeighborUpdates(Level level, List<BlockPos> toDestroy, BlockState[] affectedStates,
                                                AtomicInteger affectedIndex) {
        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos destroyedPos = toDestroy.get(i);
            BlockState destroyedState = affectedStates[affectedIndex.getAndIncrement()];

            destroyedState.updateIndirectNeighbourShapes(level, destroyedPos, UPDATE_CLIENTS);
            level.updateNeighborsAt(destroyedPos, destroyedState.getBlock());
        }
    }

    protected void taskDoMoveNeighborUpdates(Level level, List<BlockPos> toMove, BlockState[] affectedStates,
                                             AtomicInteger affectedIndex) {
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos movedPos = toMove.get(i);
            BlockState movedState = affectedStates[affectedIndex.getAndIncrement()];

            level.updateNeighborsAt(movedPos, movedState.getBlock());
        }
    }

    protected void taskDoUnMergeUpdates(Level level) {}

    public boolean run(Level level, BlockPos pos, Direction facing, boolean extend,
                       BasicStructureResolver.Factory<? extends BasicStructureResolver> structureProvider
    ) {
        if (!extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = level.getBlockState(headPos);

            if (headState.is(this.family.getHead())) {
                level.setBlock(headPos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            }
        }

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
        taskDoDestroyNeighborUpdates(level, toDestroy, affectedStates, affectedIndex);

        // do move neighbor updates
        taskDoMoveNeighborUpdates(level, toMove, affectedStates, affectedIndex);

        // do unmerge neighbor updates
        taskDoUnMergeUpdates(level);

        if (extend) {
            level.updateNeighborsAt(pos.relative(facing), this.family.getHead());
        }

        return true;
    }
}
