package ca.fxco.pistonlib.blocks.pistons.mergePiston;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.pistonHandlers.MergingPistonStructureResolver;
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

public class MergePistonBaseBlock extends BasicPistonBaseBlock {

	public MergePistonBaseBlock(PistonType type) {
        super(type);
    }

    @Override
    public PistonStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new MergingPistonStructureResolver(this, level, pos, facing, extend);
    }

    @Override
    public boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extend, StructureResolverProvider structureProvider) {
        if (!extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = level.getBlockState(headPos);

            if (headState.is(HEAD_BLOCK)) {
                level.setBlock(headPos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            }
        }

        MergingPistonStructureResolver structure = (MergingPistonStructureResolver) structureProvider.provide(level, pos, facing, extend);

        if (!structure.resolve()) {
            return false;
        }

        Map<BlockPos, BlockState> toRemove = new LinkedHashMap<>();
        List<BlockPos> toMove = structure.getToPush();
        List<BlockPos> toDestroy = structure.getToDestroy();
        List<BlockPos> toMerge = structure.getToMerge();
        List<BlockState> statesToMove = new ArrayList<>();
        List<BlockEntity> blockEntitiesToMove = new ArrayList<>();

        // collect blocks to move
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

        BlockState[] affectedStates = new BlockState[toMove.size() + toDestroy.size()];
        int affectedIndex = 0;

        float speed = 1F;
        if (toMerge.size() > 0) { // TODO: OBV NOT THE SOLUTION!!!
            BlockState temp = MOVING_BLOCK.defaultBlockState();
            BasicMovingBlockEntity movingBlockEntity = (BasicMovingBlockEntity) MOVING_BLOCK.createMovingBlockEntity(pos, temp, temp, null, facing, extend, false);
            speed = movingBlockEntity.speed();
        }

        Direction moveDir = extend ? facing : facing.getOpposite();

        // Merge Blocks
        for (int i = toMerge.size() - 1; i >= 0; i--) {
            BlockPos posToMerge = toMerge.get(i);
            BlockState stateToMerge = level.getBlockState(posToMerge);
            //BlockEntity blockEntityToMerge = level.getBlockEntity(posToMerge); //TODO: Add block entity merging api

            BlockPos mergeIntoPos = posToMerge.relative(moveDir);
            BlockState mergeIntoState = level.getBlockState(mergeIntoPos);

            level.setBlock(posToMerge, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);

            if (mergeIntoState.getBlock() instanceof MergeBlock) { // MultiMerge
                if (level.getBlockEntity(mergeIntoPos) instanceof MergeBlockEntity mergeBlockEntity) {
                    mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);
                }
            } else {

                BlockState mergeBlockState = ModBlocks.MERGE_BLOCK.defaultBlockState();
                MergeBlockEntity mergeBlockEntity = new MergeBlockEntity(mergeIntoPos, mergeBlockState, mergeIntoState); // TODO: Make it expandable like pistons later
                mergeBlockEntity.doMerge(stateToMerge, moveDir, speed);

                level.setBlock(mergeIntoPos, mergeBlockState, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
                level.setBlockEntity(mergeBlockEntity);
            }
        }

        // destroy blocks
        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos posToDestroy = toDestroy.get(i);
            BlockState stateToDestroy = level.getBlockState(posToDestroy);
            BlockEntity blockEntityToDestroy = level.getBlockEntity(posToDestroy);

            dropResources(stateToDestroy, level, posToDestroy, blockEntityToDestroy);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
            if (!stateToDestroy.is(BlockTags.FIRE)) {
                level.addDestroyBlockEffect(posToDestroy, stateToDestroy);
            }

            affectedStates[affectedIndex++] = stateToDestroy;
        }

        // move blocks
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos posToMove = toMove.get(i);
            BlockPos dstPos = posToMove.relative(moveDir);
            BlockState stateToMove = statesToMove.get(i);
            BlockEntity blockEntityToMove = blockEntitiesToMove.get(i);

            toRemove.remove(dstPos);

            BlockState movingBlock = MOVING_BLOCK.defaultBlockState()
                    .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = MOVING_BLOCK
                    .createMovingBlockEntity(dstPos, movingBlock, stateToMove, blockEntityToMove, facing, extend, false);

            level.setBlock(dstPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);

            affectedStates[affectedIndex++] = stateToMove;
        }

        // place extending head
        if (extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = HEAD_BLOCK.defaultBlockState()
                    .setValue(BasicPistonHeadBlock.TYPE, this.type)
                    .setValue(BasicPistonHeadBlock.FACING, facing);

            toRemove.remove(headPos);

            BlockState movingBlock = MOVING_BLOCK.defaultBlockState()
                    .setValue(BasicMovingBlock.TYPE, this.type)
                    .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = MOVING_BLOCK
                    .createMovingBlockEntity(headPos, movingBlock, headState, null, facing, extend, true);

            level.setBlock(headPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);
        }

        // remove left over blocks
        BlockState air = Blocks.AIR.defaultBlockState();

        for (BlockPos posToRemove : toRemove.keySet()) {
            level.setBlock(posToRemove, air, UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
        }

        // do neighbor updates
        for (Map.Entry<BlockPos, BlockState> entry : toRemove.entrySet()) {
            BlockPos removedPos = entry.getKey();
            BlockState removedState = entry.getValue();

            removedState.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            air.updateNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            air.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
        }

        affectedIndex = 0;

        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos destroyedPos = toDestroy.get(i);
            BlockState destroyedState = affectedStates[affectedIndex++];

            destroyedState.updateIndirectNeighbourShapes(level, destroyedPos, UPDATE_CLIENTS);
            level.updateNeighborsAt(destroyedPos, destroyedState.getBlock());
        }
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos movedPos = toMove.get(i);
            BlockState movedState = affectedStates[affectedIndex++];

            level.updateNeighborsAt(movedPos, movedState.getBlock());
        }
        if (extend) {
            level.updateNeighborsAt(pos.relative(facing), HEAD_BLOCK);
        }

        return true;
    }
}
