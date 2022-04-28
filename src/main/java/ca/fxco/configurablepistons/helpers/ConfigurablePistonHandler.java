package ca.fxco.configurablepistons.helpers;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

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
            if (this.retracted && blockState.getPistonBehavior() == PistonBehavior.DESTROY) { //TODO: Add custom piston behavior for destroyed here
                this.brokenBlocks.add(this.posTo);
                return true;
            }
            return false;
        } else if (this.cantMove(this.posTo, isPull ? this.motionDirection.getOpposite() : this.motionDirection)) {
            return false;
        }
        for (int i = 0; i < this.movedBlocks.size(); ++i) {
            BlockPos blockPos = this.movedBlocks.get(i);
            if (isBlockSticky(this.world.getBlockState(blockPos)) && this.cantMoveAdjacentBlock(blockPos))
                return false;
        }
        return true;
    }

    private static boolean isBlockSticky(BlockState state) {
        return state.isIn(ModTags.STICKY_BLOCKS);
    }

    private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
        if ((state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) ||
                (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK))) {
            return false;
        }
        return isBlockSticky(state) || isBlockSticky(adjacentState);
    }

    private boolean cantMove(BlockPos pos, Direction dir) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        } else if (!PistonUtils.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
            return false;
        } else if (pos.equals(this.posFrom)) {
            return false;
        } else if (this.movedBlocks.contains(pos)) {
            return false;
        }
        int i = 1;
        if (i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
        while (isBlockSticky(blockState)) {
            BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
            BlockState blockState2 = blockState;
            blockState = this.world.getBlockState(blockPos);
            if (blockState.isAir() || !isAdjacentBlockStuck(blockState2, blockState) || !PistonUtils.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite()) || blockPos.equals(this.posFrom))
                break;
            if (++i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
        }
        int j = 0, k;
        for(k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
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
                    if (isBlockSticky(this.world.getBlockState(blockPos3)) && this.cantMoveAdjacentBlock(blockPos3))
                        return true;
                }
                return false;
            }
            blockState = this.world.getBlockState(blockPos2);
            if (blockState.isAir()) return false;
            if (!PistonUtils.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom))
                return true;
            if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
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

    private boolean cantMoveAdjacentBlock(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);
                if (isAdjacentBlockStuck(blockState2, blockState) && this.cantMove(blockPos, direction)) return true;
            }
        }
        return false;
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
