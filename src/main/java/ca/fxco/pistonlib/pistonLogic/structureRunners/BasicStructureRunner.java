package ca.fxco.pistonlib.pistonLogic.structureRunners;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import java.util.*;

import static net.minecraft.world.level.block.Block.*;

/**
 * Abstract the entire piston move code into a structure runner
 */
public class BasicStructureRunner implements StructureRunner {

    protected static final BlockState AIR_STATE = Blocks.AIR.defaultBlockState();

    @Getter
    protected final Level level;
    @Getter
    protected final PistonFamily family;
    @Getter
    protected final PistonType type;
    @Getter
    protected final BlockPos blockPos;
    @Getter
    protected final Direction facing;
    @Getter
    protected final boolean extend;

    protected final Direction moveDir;
    protected final PistonStructureResolver structure;

    protected List<BlockPos> toMove;
    protected List<BlockPos> toDestroy;
    protected Map<BlockPos, BlockState> toRemove;
    protected List<BlockState> statesToMove;
    protected List<BlockEntity> blockEntitiesToMove;

    protected BlockState[] affectedStates;
    protected int affectedIndex = 0;

    public BasicStructureRunner(Level level, BlockPos pos, Direction facing, PistonFamily family, PistonType type,
                                boolean extend, BasicStructureResolver.Factory<? extends BasicStructureResolver> structureProvider) {
        this.level = level;
        this.family = family;
        this.type = type;
        this.blockPos = pos;
        this.facing = facing;
        this.moveDir = extend ? facing : facing.getOpposite();
        this.extend = extend;
        this.structure = structureProvider.create(level, pos, facing, extend);
    }

    @Override
    public void taskRemovePistonHeadOnRetract() {
        if (!extend) {
            BlockPos headPos = blockPos.relative(facing);
            BlockState headState = level.getBlockState(headPos);

            if (headState.is(family.getHead())) {
                level.setBlock(headPos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            }
        }
    }

    @Override
    public boolean taskRunStructureResolver() {
        if (!structure.resolve()) {
            return false;
        }

        // Set lists
        this.toMove = structure.getToPush();
        this.toDestroy = structure.getToDestroy();
        this.toRemove = new LinkedHashMap<>();
        this.statesToMove = new ArrayList<>();
        this.blockEntitiesToMove = new ArrayList<>();
        return true;
    }

    @Override
    public void taskSetPositionsToMove() {
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

    @Override
    public void taskMergeBlocks() {}

    @Override
    public void taskDestroyBlocks() {
        this.affectedStates = new BlockState[this.toMove.size() + this.toDestroy.size()];

        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos posToDestroy = toDestroy.get(i);
            BlockState stateToDestroy = level.getBlockState(posToDestroy);
            BlockEntity blockEntityToDestroy = level.getBlockEntity(posToDestroy);

            dropResources(stateToDestroy, level, posToDestroy, blockEntityToDestroy);
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
            if (!stateToDestroy.is(BlockTags.FIRE)) {
                level.addDestroyBlockEffect(posToDestroy, stateToDestroy);
            }

            affectedStates[affectedIndex++] = stateToDestroy;
        }
    }

    @Override
    public void taskPreventTntDuping() {
        int moveSize = toMove.size();
        if (moveSize > 0) {
            for (int i = moveSize - 1; i >= 0; i--) {
                BlockPos posToMove = toMove.get(i);

                // Get the current state from the Level
                BlockState stateToMove = level.getBlockState(posToMove);

                // Vanilla usually uses the update flags UPDATE_INVISIBLE & UPDATE_MOVE_BY_PISTON
                // Here we also add UPDATE_KNOWN_SHAPE, this removes block updates and state updates,
                // we than also add UPDATE_CLIENTS in order for shapes can be updated correctly about the block being AIR now.
                level.setBlock(posToMove, Blocks.AIR.defaultBlockState(), UPDATE_CLIENTS | UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_MOVE_BY_PISTON);

                // We replace the current state in the cached states with the latest version from the world
                this.statesToMove.set(i, stateToMove);

                // Make sure that the toRemove has the newest state also
                toRemove.put(posToMove, stateToMove);
            }
        }
    }

    @Override
    public void taskMoveBlocks() {
        int moveSize = toMove.size();
        if (moveSize > 0) {
            StructureGroup structureGroup = null;
            if (PistonLibConfig.pistonStructureGrouping && moveSize > 1) { // Only use Structure group if there are more than 1 block entities in the group
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

                affectedStates[affectedIndex++] = stateToMove;
            }
        }
    }

    @Override
    public void taskPlaceExtendingHead() {
        if (extend) {
            BlockPos headPos = blockPos.relative(facing);
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

    @Override
    public void taskRemoveLeftOverBlocks() {
        for (BlockPos posToRemove : toRemove.keySet()) {
            level.setBlock(posToRemove, AIR_STATE, UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
        }
    }

    @Override
    public void taskDoRemoveNeighborUpdates() {
        for (Map.Entry<BlockPos, BlockState> entry : toRemove.entrySet()) {
            BlockPos removedPos = entry.getKey();

            entry.getValue().updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            AIR_STATE.updateNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            AIR_STATE.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
        }
    }

    @Override
    public void taskDoDestroyNeighborUpdates() {
        affectedIndex = 0;
        // Rearrange block states so that they are in the correct order & use latest state :smartjang:
        if (PistonLibConfig.tntDupingFix) {
            int size = toDestroy.size();
            for (int i = toMove.size() - 1; i >= 0; i--) {
                affectedStates[size++] = statesToMove.get(i);
            }
        }
        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos destroyedPos = toDestroy.get(i);
            BlockState destroyedState = affectedStates[affectedIndex++];

            destroyedState.updateIndirectNeighbourShapes(level, destroyedPos, UPDATE_CLIENTS);
            level.updateNeighborsAt(destroyedPos, destroyedState.getBlock());
        }
    }

    @Override
    public void taskDoMoveNeighborUpdates() {
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos movedPos = toMove.get(i);
            BlockState movedState = affectedStates[affectedIndex++];

            level.updateNeighborsAt(movedPos, movedState.getBlock());
        }
    }

    @Override
    public void taskDoUnMergeUpdates() {}

    @Override
    public void taskDoPistonHeadExtendingUpdate() {
        if (extend) {
            level.updateNeighborsAt(blockPos.relative(facing), family.getHead());
        }
    }
}
