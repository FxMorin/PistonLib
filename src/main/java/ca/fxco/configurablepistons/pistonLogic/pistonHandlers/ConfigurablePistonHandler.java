package ca.fxco.configurablepistons.pistonLogic.pistonHandlers;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.pistonLogic.internal.AbstractBlockStateExpandedSticky;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ConfigurablePistonHandler {
    public static final int DEFAULT_MAX_MOVABLE_BLOCKS = 12;
    protected final World world;
    protected final BlockPos posFrom;
    protected final boolean retracted;
    protected final BlockPos posTo;
    protected final Direction motionDirection;
    protected final List<BlockPos> movedBlocks = Lists.newArrayList();
    protected final List<BlockPos> brokenBlocks = Lists.newArrayList();
    protected final Direction pistonDirection;
    protected final int maxMovableBlocks;

    public ConfigurablePistonHandler(World world, BlockPos pos, Direction dir, boolean retracted) {
        this(world, pos, dir, retracted, DEFAULT_MAX_MOVABLE_BLOCKS);
    }

    public ConfigurablePistonHandler(World world, BlockPos pos, Direction dir, boolean retract, int maxMovableBlocks) {
        this.world = world;
        this.posFrom = pos;
        this.pistonDirection = dir;
        this.retracted = retract;
        if (retract) {
            this.motionDirection = dir;
            this.posTo = pos.offset(dir);
        } else {
            this.motionDirection = dir.getOpposite();
            this.posTo = pos.offset(dir, 2);
        }
        this.maxMovableBlocks = maxMovableBlocks;
    }

    public boolean calculatePullPush(boolean isPull) {
        this.movedBlocks.clear();
        this.brokenBlocks.clear();
        BlockState state = this.world.getBlockState(this.posTo);
        if (!PistonUtils.isMovable(state, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
            if (this.retracted) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
                if (pistonBehavior.usesConfigurablePistonBehavior()) {
                    if (pistonBehavior.canDestroy(state)) {
                        this.brokenBlocks.add(this.posTo);
                        return true;
                    }
                } else if (state.getPistonBehavior() == PistonBehavior.DESTROY) {
                    this.brokenBlocks.add(this.posTo);
                    return true;
                }
                return false;
            }
            return false;
        } else {
            if (this.cantMove(this.posTo, isPull ? this.motionDirection.getOpposite() : this.motionDirection))
                return false;
        }
        for (int i = 0; i < this.movedBlocks.size(); ++i) {
            BlockPos blockPos = this.movedBlocks.get(i);
            state = this.world.getBlockState(blockPos);
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

    protected static boolean isBlockSticky(BlockState state) {
        return ((AbstractBlockStateExpandedSticky)state).getStickyGroup() != null;
    }

    protected boolean cantMoveAdjacentBlocks(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, blockState, blockState2) && this.cantMove(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveAdjacentStickyBlocks(Map<Direction, StickyType> sides, BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        for (Map.Entry<Direction,StickyType> sideData : sides.entrySet()) {
            StickyType stickyType = sideData.getValue();
            if (stickyType == StickyType.NO_STICK) continue;
            Direction direction = sideData.getKey();
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, blockState, blockState2) && this.cantMove(blockPos, direction))
                    return true;
            }
        }
        return false;
    }


    // Stickiness checks
    protected static boolean canAdjacentBlockStick(Direction dir, BlockState state, BlockState adjState) {
        AbstractBlockStateExpandedSticky stick = (AbstractBlockStateExpandedSticky)adjState;
        if (stick.usesConfigurablePistonStickiness())
            return !stick.isSticky() ||
                    stick.sideStickiness(dir.getOpposite()) != StickyType.NO_STICK;
        return StickyGroup.canStick(((AbstractBlockStateExpandedSticky)state).getStickyGroup(), stick.getStickyGroup());
    }

    protected boolean cantMove(BlockPos pos, Direction dir) {
        BlockState state = this.world.getBlockState(pos);
        if (state.isAir() || pos.equals(this.posFrom) || this.movedBlocks.contains(pos)) return false;
        if (!PistonUtils.isMovable(state, this.world, pos, this.motionDirection, false, dir)) return false;
        int i = 1;
        if (i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
        Direction dir2 = this.motionDirection.getOpposite();
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)state.getBlock();
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, dir2).ordinal() >= StickyType.STICKY.ordinal()) :
                stick.hasStickyGroup();
        while (isSticky) {
            BlockPos blockPos = pos.offset(dir2, i);
            BlockState blockState2 = state;
            state = this.world.getBlockState(blockPos);
            stick = (ConfigurablePistonStickiness)state.getBlock();
            if (state.isAir() ||
                    !canAdjacentBlockStick(dir2, blockState2, state) ||
                    blockPos.equals(this.posFrom) ||
                    !PistonUtils.isMovable(state, this.world, blockPos, this.motionDirection, false, dir2))
                break;
            if (++i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
            if (stick.usesConfigurablePistonStickiness()) {
                boolean StickyStick = stick.isSticky(state);
                if (StickyStick && stick.sideStickiness(state, dir2).ordinal() < StickyType.STICKY.ordinal())
                    break;
                isSticky = StickyStick;
            } else {
                isSticky = stick.hasStickyGroup();
            }
        }
        int j = 0, k;
        for(k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(pos.offset(dir2, k));
            ++j;
        }
        k = 1;
        while(true) {
            BlockPos pos2 = pos.offset(this.motionDirection, k);
            int l = this.movedBlocks.indexOf(pos2);
            if (l > -1) {
                this.setMovedBlocks(j, l);
                for(int m = 0; m <= l + j; ++m) {
                    BlockPos pos3 = this.movedBlocks.get(m);
                    state = this.world.getBlockState(pos3);
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
            state = this.world.getBlockState(pos2);
            if (state.isAir())
                return false;
            if (pos2.equals(this.posFrom))
                return true;
            if (!PistonUtils.isMovable(state, this.world, pos2, this.motionDirection, true, this.motionDirection))
                return true;
            ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (pistonBehavior.usesConfigurablePistonBehavior()) {
                if (pistonBehavior.canDestroy(state)) {
                    this.brokenBlocks.add(pos2);
                    return false;
                }
            } else if (state.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(pos2);
                return false;
            }
            if (this.movedBlocks.size() >= this.maxMovableBlocks) return true;
            this.movedBlocks.add(pos2);
            ++j;
            ++k;
        }
    }

    protected void setMovedBlocks(int from, int to) {
        List<BlockPos> list = Lists.newArrayList();
        List<BlockPos> list2 = Lists.newArrayList();
        List<BlockPos> list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, to));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    public Direction getMotionDirection() {
        return this.motionDirection;
    }

    public List<BlockPos> getMovedBlocks() {
        return this.movedBlocks;
    }

    public List<BlockPos> getBrokenBlocks() {
        return this.brokenBlocks;
    }

    public int getMaxMovableBlocks() {
        return this.maxMovableBlocks;
    }
}
