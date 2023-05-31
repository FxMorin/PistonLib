package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import java.util.ArrayList;
import java.util.List;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlock;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class MergingPistonStructureResolver extends BasicStructureResolver {

    public final List<BlockPos> toMerge = new ArrayList<>();
    public final List<BlockPos> toUnMerge = new ArrayList<>();
    public final List<BlockPos> ignore = new ArrayList<>();

    public MergingPistonStructureResolver(BasicPistonBaseBlock piston, Level level, BlockPos pos, Direction facing, int length, boolean extend) {
        super(piston, level, pos, facing, length, extend);
    }

    @Override
    protected void resetResolver() {
        super.resetResolver();
        this.toMerge.clear();
        this.toUnMerge.clear();
        this.ignore.clear();
    }

    @Override
    protected boolean runStructureGeneration() {
        if (super.runStructureGeneration()) {
            this.toUnMerge.removeAll(this.ignore); // Remove ignored blocks from toUnMerge list
            return true;
        }
        return false;
    }

    protected boolean cantMove(BlockPos pos, Direction dir) {
        BlockState state = this.level.getBlockState(pos);
        if (state.isAir() ||
                pos.equals(this.pistonPos) ||
                this.toPush.contains(pos) ||
                this.toMerge.contains(pos) ||
                this.ignore.contains(pos)) {
            return false;
        }
        if (!this.piston.canMoveBlock(state, this.level, pos, this.pushDirection, false, dir)) {
            return false;
        }
        int weight = state.pl$getWeight();
        if (weight + this.movingWeight > this.maxMovableWeight) {
            return true;
        }
        Direction pushDirOpposite = this.pushDirection.getOpposite();
        boolean initialBlock = pos.relative(pushDirOpposite).equals(this.pistonPos);

        // UnMerge checks on initial line blocks
        if (!initialBlock) {
            if (state.pl$usesConfigurablePistonMerging()) {
                BlockState neighborState = level.getBlockState(pos.relative(pushDirOpposite));
                if (state.pl$canUnMerge(level, pos, neighborState, this.pushDirection) &&
                    (!state.pl$getBlockEntityMergeRules().checkUnMerge() ||
                    level.getBlockEntity(pos).pl$canUnMerge(state, neighborState, this.pushDirection))) {
                    if (this.toUnMerge.contains(pos)) {
                        // If multiple sticky blocks are moving the same block, don't unmerge
                        this.ignore.add(pos);
                    } else {
                        this.toUnMerge.add(pos);
                    }
                }
            }
        }

        // Do sticky checks on initial line blocks
        int distance = 1;
        BlockPos lastBlockPos = pos;
        while (isSticky(state, pushDirOpposite)) {
            BlockPos blockPos = pos.relative(pushDirOpposite, distance);
            BlockState lastState = state;
            state = this.level.getBlockState(blockPos);

            if (state.isAir() ||
                    !canAdjacentBlockStick(pushDirOpposite, lastState, state) ||
                    blockPos.equals(this.pistonPos) ||
                    this.toMerge.contains(blockPos) ||
                    this.ignore.contains(blockPos) ||
                    !this.piston.canMoveBlock(state, this.level, blockPos, this.pushDirection, false, pushDirOpposite)) {
                break;
            }
            weight += state.pl$getWeight();
            if (weight + this.movingWeight > this.maxMovableWeight) {
                return true;
            }
            ++distance;

            // UnMerge checks
            if (state.pl$usesConfigurablePistonMerging() &&
                    state.pl$canUnMerge(level, blockPos, lastState, this.pushDirection)
                    && !this.toPush.contains(lastBlockPos) &&
                    (!state.pl$getBlockEntityMergeRules().checkUnMerge() ||
                    level.getBlockEntity(blockPos).pl$canUnMerge(state, lastState, this.pushDirection))) {
                if (this.toUnMerge.contains(blockPos)) {
                    // If multiple sticky blocks are moving the same block, don't unmerge
                    this.ignore.add(blockPos);
                } else {
                    this.toUnMerge.add(blockPos);
                }
            }

            lastBlockPos = blockPos;
        }
        this.movingWeight += weight;
        for(int k = distance - 1; k >= 0; --k) {
            this.toPush.add(pos.relative(pushDirOpposite, k));
        }
        int nextIndex = 1;
        BlockState lastState;
        lastBlockPos = pos;
        BlockPos currentPos = pos.relative(this.pushDirection, nextIndex);
        while(true) {
            lastState = state;

            // Sticky Checks
            int lastIndex = this.toPush.indexOf(currentPos);
            if (lastIndex > -1) {
                this.setMovedBlocks(distance, lastIndex);
                for(int m = 0; m <= lastIndex + distance; ++m) {
                    BlockPos pos3 = this.toPush.get(m);
                    state = this.level.getBlockState(pos3);
                    if (!attemptMove(state, pos3)) {
                        return true;
                    }
                }
                return false;
            }

            state = this.level.getBlockState(currentPos);

            // Merge checks
            if (state.getBlock() instanceof MergeBlock) { // MultiMerge
                if (lastState.pl$usesConfigurablePistonMerging() &&
                        lastState.pl$canMergeFromSide(level, lastBlockPos, pushDirOpposite)) {
                    if (level.getBlockEntity(currentPos) instanceof MergeBlockEntity mergeBlockEntity &&
                            mergeBlockEntity.canMergeFromSide(this.pushDirection) &&
                            mergeBlockEntity.canMerge(state, this.pushDirection)) {
                        this.toMerge.add(lastBlockPos);
                        this.toPush.remove(lastBlockPos);
                        this.ignore.add(currentPos);
                        return false;
                    }
                }
            } else {
                if (state.pl$usesConfigurablePistonMerging()) {
                    if (state.pl$canMerge(level, currentPos, lastState, this.pushDirection)) {
                        if ((!lastState.pl$usesConfigurablePistonMerging() ||
                                lastState.pl$canMergeFromSide(level, lastBlockPos, pushDirOpposite)) &&
                                (!state.pl$getBlockEntityMergeRules().checkMerge() ||
                                level.getBlockEntity(currentPos).pl$canMerge(state, lastState, this.pushDirection))) {
                            this.toMerge.add(lastBlockPos);
                            this.toPush.remove(lastBlockPos);
                            this.ignore.add(currentPos);
                            return false;
                        }
                    }
                    if (!this.toPush.contains(lastBlockPos) &&
                            state.pl$canUnMerge(level, currentPos, lastState, this.pushDirection) &&
                            (!state.pl$getBlockEntityMergeRules().checkUnMerge() ||
                            level.getBlockEntity(currentPos).pl$canUnMerge(state, lastState, this.pushDirection))) {
                        if (this.toUnMerge.contains(currentPos)) {
                            // If multiple sticky blocks are moving the same block, don't unmerge
                            this.ignore.add(currentPos);
                        } else {
                            this.toUnMerge.add(currentPos);
                        }
                    }
                }
            }

            // Movement Checks
            if (state.isAir()) {
                return false;
            } else if (currentPos.equals(this.pistonPos)) {
                return true;
            } else if (!piston.canMoveBlock(state, this.level, currentPos, this.pushDirection, true, this.pushDirection)) {
                return true;
            }
            if (state.pl$usesConfigurablePistonBehavior()) {
                if (state.pl$canDestroy(this.level, currentPos)) {
                    this.toDestroy.add(currentPos);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(currentPos);
                return false;
            }
            weight = state.pl$getWeight();
            if (weight + this.movingWeight > this.maxMovableWeight) {
                return true;
            }
            this.movingWeight += weight;

            ++distance;
            ++nextIndex;

            lastBlockPos = currentPos;
            currentPos = pos.relative(this.pushDirection, nextIndex);

            // This check makes sure that if another block is going to push it from behind, it can't unmerge
            if (this.toUnMerge.contains(currentPos)) { // currentPos is actually nextPos until after toPush.add() runs
                this.ignore.add(currentPos);
            }

            this.toPush.add(lastBlockPos);
        }
    }

    public List<BlockPos> getToMerge() {
        return this.toMerge;
    }

    public List<BlockPos> getToUnMerge() {
        return this.toUnMerge;
    }
}
