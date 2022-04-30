package ca.fxco.configurablepistons.helpers;

import ca.fxco.configurablepistons.base.ModTags;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ConfigurablePistonHandler {
    public static final int DEFAULT_MAX_MOVABLE_BLOCKS = 12;
    private final World world;
    private final BlockPos posFrom;
    private final boolean retracted;
    private final BlockPos posTo;
    private final Direction motionDirection;
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();
    private final Direction pistonDirection;
    private final int maxMovableBlocks;

    public ConfigurablePistonHandler(World world, BlockPos pos, Direction dir, boolean retracted) {
        this(world, pos, dir, retracted, DEFAULT_MAX_MOVABLE_BLOCKS);
    }

    public ConfigurablePistonHandler(World world, BlockPos pos, Direction dir, boolean retracted, int maxMovableBlocks) {
        this.world = world;
        this.posFrom = pos;
        this.pistonDirection = dir;
        this.retracted = retracted;
        if (retracted) {
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
        BlockState blockState = this.world.getBlockState(this.posTo);
        if (!PistonUtils.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
            if (this.retracted) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)blockState.getBlock();
                if (pistonBehavior.usesConfigurablePistonBehavior()) {
                    if (pistonBehavior.canDestroy(blockState)) {
                        this.brokenBlocks.add(this.posTo);
                        return true;
                    }
                } else if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
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
            blockState = this.world.getBlockState(blockPos);
            ConfigurablePistonStickiness stickiness = (ConfigurablePistonStickiness)blockState.getBlock();
            if (stickiness.usesConfigurablePistonStickiness()) {
                if (stickiness.isSticky(blockState) && cantMoveAdjacentStickyBlocks(stickiness.stickySides(blockState), blockPos))
                    return false;
            } else {
                if (isBlockSticky(this.world.getBlockState(blockPos)) && this.cantMoveAdjacentBlocks(blockPos))
                    return false;
            }
        }
        return true;
    }

    private static boolean isBlockSticky(BlockState state) {
        return state.isIn(ModTags.STICKY_BLOCKS);
    }

    private boolean cantMoveAdjacentBlocks(BlockPos pos) {
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

    private boolean cantMoveAdjacentStickyBlocks(Map<Direction, StickyType> sides, BlockPos pos) {
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
    private static boolean canAdjacentBlockStick(Direction direction, BlockState state, BlockState adjacentState) {
        ConfigurablePistonStickiness stickiness = (ConfigurablePistonStickiness)adjacentState.getBlock();
        if (stickiness.usesConfigurablePistonStickiness())
            return !stickiness.isSticky(adjacentState) || stickiness.sideStickiness(adjacentState, direction.getOpposite()) != StickyType.NO_STICK;
        // TODO: Make this configurable
        return (!state.isOf(Blocks.HONEY_BLOCK) || !adjacentState.isOf(Blocks.SLIME_BLOCK)) &&
                (!state.isOf(Blocks.SLIME_BLOCK) || !adjacentState.isOf(Blocks.HONEY_BLOCK));
    }

    private boolean cantMove(BlockPos pos, Direction dir) {
        BlockState state = this.world.getBlockState(pos);
        if (state.isAir()) {
            return false;
        } else if (!PistonUtils.isMovable(state, this.world, pos, this.motionDirection, false, dir)) {
            return false;
        } else if (pos.equals(this.posFrom)) {
            return false;
        } else if (this.movedBlocks.contains(pos)) {
            return false;
        }
        int i = 1;
        if (i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
        Direction direction = this.motionDirection.getOpposite();
        ConfigurablePistonStickiness stickiness = (ConfigurablePistonStickiness)state.getBlock();
        boolean isSticky;
        if (stickiness.usesConfigurablePistonStickiness()) {
            if (stickiness.sideStickiness(state, direction).ordinal() < StickyType.STICKY.ordinal()) {
                isSticky = false;
            } else {
                isSticky = stickiness.isSticky(state);
            }
        } else {
            isSticky = isBlockSticky(state);
        }
        while (isSticky) {
            BlockPos blockPos = pos.offset(direction, i);
            BlockState blockState2 = state;
            state = this.world.getBlockState(blockPos);
            stickiness = (ConfigurablePistonStickiness)state.getBlock();
            if (state.isAir() || !canAdjacentBlockStick(direction, blockState2, state) || blockPos.equals(this.posFrom) || !PistonUtils.isMovable(state, this.world, blockPos, this.motionDirection, false, direction))
                break;
            if (++i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
            if (stickiness.usesConfigurablePistonStickiness()) {
                if (stickiness.sideStickiness(state, direction).ordinal() < StickyType.STICKY.ordinal())
                    break;
                isSticky = stickiness.isSticky(state);
            } else {
                isSticky = isBlockSticky(state);
            }
        }
        int j = 0, k;
        for(k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(pos.offset(direction, k));
            ++j;
        }
        k = 1;
        while(true) {
            BlockPos blockPos2 = pos.offset(this.motionDirection, k);
            int l = this.movedBlocks.indexOf(blockPos2);
            if (l > -1) {
                this.setMovedBlocks(j, l);
                for(int m = 0; m <= l + j; ++m) {
                    BlockPos blockPos3 = this.movedBlocks.get(m);
                    state = this.world.getBlockState(blockPos3);
                    stickiness = (ConfigurablePistonStickiness)state.getBlock();
                    if (stickiness.usesConfigurablePistonStickiness()) {
                        if (stickiness.isSticky(state) && this.cantMoveAdjacentStickyBlocks(stickiness.stickySides(state),blockPos3))
                            return true;
                    } else {
                        if (isBlockSticky(state) && this.cantMoveAdjacentBlocks(blockPos3))
                            return true;
                    }
                }
                return false;
            }
            state = this.world.getBlockState(blockPos2);
            if (state.isAir()) return false;
            if (!PistonUtils.isMovable(state, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom))
                return true;
            ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (pistonBehavior.usesConfigurablePistonBehavior()) {
                if (pistonBehavior.canDestroy(state)) {
                    this.brokenBlocks.add(blockPos2);
                    return false;
                }
            } else if (state.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(blockPos2);
                return false;
            }
            if (this.movedBlocks.size() >= this.maxMovableBlocks) return true;
            this.movedBlocks.add(blockPos2);
            ++j;
            ++k;
        }
    }

    private void setMovedBlocks(int from, int to) {
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
