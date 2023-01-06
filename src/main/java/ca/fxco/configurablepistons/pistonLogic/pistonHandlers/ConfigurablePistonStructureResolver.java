package ca.fxco.configurablepistons.pistonLogic.pistonHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.pistonLogic.internal.BlockStateBaseExpandedSticky;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ConfigurablePistonStructureResolver {

    public static final int DEFAULT_MAX_MOVABLE_BLOCKS = 12;

    protected final Level level;
    protected final BlockPos pistonPos;
    protected final boolean extend;
    protected final BlockPos startPos;
    protected final Direction moveDirection;
    protected final List<BlockPos> toMove = new ArrayList<>();
    protected final List<BlockPos> toDestroy = new ArrayList<>();
    protected final Direction pistonFacing;
    protected final int maxMovableBlocks;

    public ConfigurablePistonStructureResolver(Level level, BlockPos pistonPos, Direction pistonFacing, boolean extend) {
        this(level, pistonPos, pistonFacing, extend, DEFAULT_MAX_MOVABLE_BLOCKS);
    }

    public ConfigurablePistonStructureResolver(Level level, BlockPos pistonPos, Direction pistonFacing, boolean extend, int maxMovableBlocks) {
        this.level = level;
        this.pistonPos = pistonPos;
        this.pistonFacing = pistonFacing;
        this.extend = extend;
        if (extend) {
            this.moveDirection = pistonFacing;
            this.startPos = pistonPos.relative(pistonFacing);
        } else {
            this.moveDirection = pistonFacing.getOpposite();
            this.startPos = pistonPos.relative(pistonFacing, 2);
        }
        this.maxMovableBlocks = maxMovableBlocks;
    }

    public boolean resolve(boolean isPull) {
        this.toMove.clear();
        this.toDestroy.clear();
        BlockState state = this.level.getBlockState(this.startPos);
        if (!PistonUtils.isMovable(state, this.level, this.startPos, this.moveDirection, false, this.pistonFacing)) {
            if (this.extend) {
                ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior) state.getBlock();
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
            if (this.cantMove(this.startPos, isPull ? this.moveDirection.getOpposite() : this.moveDirection))
                return false;
        }
        for (int i = 0; i < this.toMove.size(); ++i) {
            BlockPos blockPos = this.toMove.get(i);
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

    protected boolean cantMoveAdjacentBlocks(BlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.moveDirection.getAxis()) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState2 = this.level.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, blockState, blockState2) && this.cantMove(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveAdjacentStickyBlocks(Map<Direction, StickyType> sides, BlockPos pos) {
        BlockState blockState = this.level.getBlockState(pos);
        for (Map.Entry<Direction, StickyType> sideData : sides.entrySet()) {
            StickyType stickyType = sideData.getValue();
            if (stickyType == StickyType.NO_STICK) continue;
            Direction dir = sideData.getKey();
            if (dir.getAxis() != this.moveDirection.getAxis()) {
                BlockPos blockPos = pos.relative(dir);
                BlockState adjState = this.level.getBlockState(blockPos);
                if (stickyType == StickyType.CONDITIONAL && !stickyType.canStick(blockState, adjState, dir)) {
                    continue;
                }
                if (canAdjacentBlockStick(dir, blockState, adjState) && this.cantMove(blockPos, dir))
                    return true;
            }
        }
        return false;
    }


    // Stickiness checks
    protected static boolean canAdjacentBlockStick(Direction dir, BlockState state, BlockState adjState) {
        BlockStateBaseExpandedSticky stick = (BlockStateBaseExpandedSticky)adjState;
        if (stick.usesConfigurablePistonStickiness()) {
            if (!stick.isSticky()) return true;
            StickyType type = stick.sideStickiness(dir.getOpposite());
            if (type == StickyType.CONDITIONAL && !type.canStick(state, adjState, dir)) {
                return true;
            }
            return type != StickyType.NO_STICK;
        }
        return StickyGroup.canStick(((BlockStateBaseExpandedSticky)state).getStickyGroup(), stick.getStickyGroup());
    }

    protected boolean cantMove(BlockPos pos, Direction dir) {
        BlockState state = this.level.getBlockState(pos);
        if (state.isAir() || pos.equals(this.pistonPos) || this.toMove.contains(pos)) return false;
        if (!PistonUtils.isMovable(state, this.level, pos, this.moveDirection, false, dir)) return false;
        int i = 1;
        if (i + this.toMove.size() > this.maxMovableBlocks) return true;
        Direction dir2 = this.moveDirection.getOpposite();
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)state.getBlock();
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, dir2).ordinal() >= StickyType.STICKY.ordinal()) :
                stick.hasStickyGroup();
        while (isSticky) {
            BlockPos blockPos = pos.relative(dir2, i);
            BlockState blockState2 = state;
            state = this.level.getBlockState(blockPos);
            stick = (ConfigurablePistonStickiness)state.getBlock();
            if (state.isAir() ||
                    !canAdjacentBlockStick(dir2, blockState2, state) ||
                    blockPos.equals(this.pistonPos) ||
                    !PistonUtils.isMovable(state, this.level, blockPos, this.moveDirection, false, dir2))
                break;
            if (++i + this.toMove.size() > this.maxMovableBlocks) return true;
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
            this.toMove.add(pos.relative(dir2, k));
            ++j;
        }
        k = 1;
        while(true) {
            BlockPos pos2 = pos.relative(this.moveDirection, k);
            int l = this.toMove.indexOf(pos2);
            if (l > -1) {
                this.setMovedBlocks(j, l);
                for(int m = 0; m <= l + j; ++m) {
                    BlockPos pos3 = this.toMove.get(m);
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
            state = this.level.getBlockState(pos2);
            if (state.isAir())
                return false;
            if (pos2.equals(this.pistonPos))
                return true;
            if (!PistonUtils.isMovable(state, this.level, pos2, this.moveDirection, true, this.moveDirection))
                return true;
            ConfigurablePistonBehavior pistonBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (pistonBehavior.usesConfigurablePistonBehavior()) {
                if (pistonBehavior.canDestroy(state)) {
                    this.toDestroy.add(pos2);
                    return false;
                }
            } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(pos2);
                return false;
            }
            if (this.toMove.size() >= this.maxMovableBlocks) return true;
            this.toMove.add(pos2);
            ++j;
            ++k;
        }
    }

    protected void setMovedBlocks(int from, int to) {
        List<BlockPos> list = new ArrayList<>();
        List<BlockPos> list2 = new ArrayList<>();
        List<BlockPos> list3 = new ArrayList<>();
        list.addAll(this.toMove.subList(0, to));
        list2.addAll(this.toMove.subList(this.toMove.size() - from, this.toMove.size()));
        list3.addAll(this.toMove.subList(to, this.toMove.size() - from));
        this.toMove.clear();
        this.toMove.addAll(list);
        this.toMove.addAll(list2);
        this.toMove.addAll(list3);
    }

    public Direction getMoveDirection() {
        return this.moveDirection;
    }

    public List<BlockPos> getToMove() {
        return this.toMove;
    }

    public List<BlockPos> getToDestroy() {
        return this.toDestroy;
    }

    public int getMaxMovableBlocks() {
        return this.maxMovableBlocks;
    }
}
