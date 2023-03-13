package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import java.util.ArrayList;
import java.util.List;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlock;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.impl.BlockEntityMerging;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergePistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class MergingPistonStructureResolver extends BasicStructureResolver {

    public final List<BlockPos> toMerge = new ArrayList<>();
    public final List<BlockPos> toUnMerge = new ArrayList<>();
    public final List<BlockPos> ignore = new ArrayList<>();

    public MergingPistonStructureResolver(MergePistonBaseBlock piston, Level level, BlockPos pos, Direction facing, int length, boolean extend) {
        super(piston, level, pos, facing, length, extend);
    }

    @Override
    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        this.toMerge.clear();
        this.toUnMerge.clear();
        this.ignore.clear();
        BlockState state = this.level.getBlockState(this.startPos);
        if (!this.piston.canMoveBlock(state, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
                if (pistonBehavior.usesConfigurablePistonBehavior()) {
                    if (pistonBehavior.canDestroy(this.level, this.startPos, state)) {
                        this.toDestroy.add(this.startPos);
                        return true;
                    }
                } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                    this.toDestroy.add(this.startPos);
                    return true;
                }
                return false;
            }
            return false;
        } else {
            if (this.cantMove(this.startPos, !this.extending ? this.pushDirection.getOpposite() : this.pushDirection))
                return false;
        }
        for (int i = 0; i < this.toPush.size(); ++i) {
            BlockPos blockPos = this.toPush.get(i);
            state = this.level.getBlockState(blockPos);
            ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
            if (stick.usesConfigurablePistonStickiness()) {
                if (stick.isSticky(state) && cantMoveAdjacentStickyBlocks(stick.stickySides(state), blockPos))
                    return false;
            } else {
                if (stick.hasStickyGroup() && this.cantMoveAdjacentBlocks(blockPos))
                    return false;
            }
        }
        this.toUnMerge.removeAll(this.ignore); // Remove ignored blocks from toUnMerge list
        return true;
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
        if (!this.piston.canMoveBlock(state, this.level, pos, this.pushDirection, false, dir))
            return false;
        if (1 + this.toPush.size() > this.maxMovableBlocks)
            return true;
        Direction pushDirOpposite = this.pushDirection.getOpposite();
        boolean initialBlock = pos.relative(pushDirOpposite).equals(this.pistonPos);

        // UnMerge checks on initial line blocks
        if (!initialBlock) {
            ConfigurablePistonMerging merge = (ConfigurablePistonMerging) state.getBlock();
            if (merge.usesConfigurablePistonMerging()) {
                BlockState neighborState = level.getBlockState(pos.relative(pushDirOpposite));
                if (merge.canUnMerge(state, level, pos, neighborState, this.pushDirection) &&
                    (!merge.getBlockEntityMergeRules().checkUnMerge() ||
                    (!(level.getBlockEntity(pos) instanceof BlockEntityMerging bem) ||
                    bem.canUnMerge(state, neighborState, this.pushDirection)))) {
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
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, pushDirOpposite).ordinal() >= StickyType.STICKY.ordinal()) :
                stick.hasStickyGroup();
        int distance = 1;
        BlockPos lastBlockPos = pos;
        while (isSticky) {
            BlockPos blockPos = pos.relative(pushDirOpposite, distance);
            BlockState lastState = state;
            state = this.level.getBlockState(blockPos);

            stick = (ConfigurablePistonStickiness)state.getBlock();
            if (state.isAir() ||
                    !canAdjacentBlockStick(pushDirOpposite, lastState, state) ||
                    blockPos.equals(this.pistonPos) ||
                    this.toMerge.contains(blockPos) ||
                    this.ignore.contains(blockPos) ||
                    !this.piston.canMoveBlock(state, this.level, blockPos, this.pushDirection, false, pushDirOpposite))
                break;
            if (++distance + this.toPush.size() > this.maxMovableBlocks)
                return true;
            if (stick.usesConfigurablePistonStickiness()) {
                boolean StickyStick = stick.isSticky(state);
                if (StickyStick && stick.sideStickiness(state, pushDirOpposite).ordinal() < StickyType.STICKY.ordinal())
                    break;
                isSticky = StickyStick;
            } else {
                isSticky = stick.hasStickyGroup();
            }

            // UnMerge checks
            ConfigurablePistonMerging merge = (ConfigurablePistonMerging) state.getBlock();
            if (merge.usesConfigurablePistonMerging() &&
                    merge.canUnMerge(state, level, blockPos, lastState, this.pushDirection)
                    && !this.toPush.contains(lastBlockPos) &&
                    (!merge.getBlockEntityMergeRules().checkUnMerge() ||
                    (!(level.getBlockEntity(blockPos) instanceof BlockEntityMerging bem) ||
                    bem.canUnMerge(state, lastState, this.pushDirection)))) {
                if (this.toUnMerge.contains(blockPos)) {
                    // If multiple sticky blocks are moving the same block, don't unmerge
                    this.ignore.add(blockPos);
                } else {
                    this.toUnMerge.add(blockPos);
                }
            }

            lastBlockPos = blockPos;
        }
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
                    stick = (ConfigurablePistonStickiness)state.getBlock();
                    if (stick.usesConfigurablePistonStickiness()) {
                        if (stick.isSticky(state) && this.cantMoveAdjacentStickyBlocks(stick.stickySides(state),pos3))
                            return true;
                    } else {
                        if (stick.hasStickyGroup() && this.cantMoveAdjacentBlocks(pos3))
                            return true;
                    }
                }
                return false;
            }

            state = this.level.getBlockState(currentPos);

            // Merge checks
            if (state.getBlock() instanceof MergeBlock) { // MultiMerge
                ConfigurablePistonMerging merge = (ConfigurablePistonMerging) lastState.getBlock();
                if (merge.usesConfigurablePistonMerging() &&
                        merge.canMergeFromSide(lastState, level, lastBlockPos, pushDirOpposite)) {
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
                ConfigurablePistonMerging merge = (ConfigurablePistonMerging) state.getBlock();
                if (merge.usesConfigurablePistonMerging()) {
                    if (merge.canMerge(state, level, currentPos, lastState, this.pushDirection)) {
                        ConfigurablePistonMerging lastMerge = (ConfigurablePistonMerging) lastState.getBlock();
                        if ((!lastMerge.usesConfigurablePistonMerging() ||
                                lastMerge.canMergeFromSide(lastState, level, lastBlockPos, pushDirOpposite)) &&
                                (!merge.getBlockEntityMergeRules().checkMerge() ||
                                (!(level.getBlockEntity(currentPos) instanceof BlockEntityMerging currentBem) ||
                                        currentBem.canMerge(state, lastState, this.pushDirection)))) {
                            this.toMerge.add(lastBlockPos);
                            this.toPush.remove(lastBlockPos);
                            this.ignore.add(currentPos);
                            return false;
                        }
                    }
                    if (!this.toPush.contains(lastBlockPos) &&
                            merge.canUnMerge(state, level, currentPos, lastState, this.pushDirection) &&
                            (!merge.getBlockEntityMergeRules().checkUnMerge() ||
                            (!(level.getBlockEntity(currentPos) instanceof BlockEntityMerging bem) ||
                            bem.canUnMerge(state, lastState, this.pushDirection)))) {
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
            if (state.isAir())
                return false;
            if (currentPos.equals(this.pistonPos))
                return true;
            if (!piston.canMoveBlock(state, this.level, currentPos, this.pushDirection, true, this.pushDirection)) {
                return true;
            }
            ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (pistonBehavior.usesConfigurablePistonBehavior()) {
                if (pistonBehavior.canDestroy(this.level, currentPos, state)) {
                    this.toDestroy.add(currentPos);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(currentPos);
                return false;
            }
            if (this.toPush.size() >= this.maxMovableBlocks)
                return true;

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
