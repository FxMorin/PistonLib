package ca.fxco.pistonlib.pistonLogic.pistonHandlers;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlock;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;

public class MergingPistonStructureResolver extends ConfigurablePistonStructureResolver {

    public final List<BlockPos> toMerge = Lists.newArrayList();
    public final List<BlockPos> ignore = Lists.newArrayList();

    public MergingPistonStructureResolver(BasicPistonBaseBlock piston, Level level, BlockPos pos, Direction facing, boolean extend) {
        super(piston, level, pos, facing, extend);
    }

    @Override
    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        this.toMerge.clear(); // clear merge
        this.ignore.clear();
        BlockState state = this.level.getBlockState(this.startPos);
        if (!this.piston.canMoveBlock(state, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
                if (pistonBehavior.usesConfigurablePistonBehavior()) {
                    if (pistonBehavior.canDestroy(state)) {
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
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, pushDirOpposite).ordinal() >= StickyType.STICKY.ordinal()) :
                stick.hasStickyGroup();
        int distance = 1;
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
        }
        for(int k = distance - 1; k >= 0; --k) {
            this.toPush.add(pos.relative(pushDirOpposite, k));
        }
        int nextIndex = 1;
        BlockState lastState;
        BlockPos lastBlockPos = pos;
        while(true) {
            BlockPos currentPos = pos.relative(this.pushDirection, nextIndex);
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
                if (merge.usesConfigurablePistonMerging() && merge.canMergeFromSide(lastState, pushDirOpposite)) {
                    if (level.getBlockEntity(currentPos) instanceof MergeBlockEntity mergeBlockEntity) {
                        if (mergeBlockEntity.canMergeFromSide(this.pushDirection)) {
                            if (mergeBlockEntity.canMerge(state, this.pushDirection)) {
                                this.toMerge.add(lastBlockPos);
                                this.toPush.remove(lastBlockPos);
                                this.ignore.add(currentPos);
                                return false;
                            }
                        }
                    }
                }
            } else {
                ConfigurablePistonMerging merge = (ConfigurablePistonMerging) state.getBlock();
                if (merge.usesConfigurablePistonMerging() && merge.canMergeFromSide(state, this.pushDirection)) {
                    ConfigurablePistonMerging lastMerge = (ConfigurablePistonMerging) lastState.getBlock();
                    if (lastMerge.usesConfigurablePistonMerging() && lastMerge.canMergeFromSide(lastState, pushDirOpposite)) {
                        if (merge.canMerge(lastState, state, this.pushDirection)) {
                            this.toMerge.add(lastBlockPos);
                            this.toPush.remove(lastBlockPos);
                            this.ignore.add(currentPos);
                            return false;
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
                if (pistonBehavior.canDestroy(state)) {
                    this.toDestroy.add(currentPos);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(currentPos);
                return false;
            }
            if (this.toPush.size() >= this.maxMovableBlocks)
                return true;
            this.toPush.add(currentPos);
            ++distance;
            ++nextIndex;
            lastBlockPos = currentPos;
        }
    }

    public List<BlockPos> getToMerge() {
        return this.toMerge;
    }
}
