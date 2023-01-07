package ca.fxco.pistonlib.pistonLogic.pistonHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.longPiston.LongMovingBlockEntity;
import ca.fxco.pistonlib.pistonLogic.PistonUtils;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ConfigurableLongPistonHandler extends ConfigurablePistonStructureResolver {

    protected final BasicMovingBlock extensionBlock;

    public ConfigurableLongPistonHandler(Level world, BlockPos pos, Direction dir,
                                         boolean retracted, BasicMovingBlock extensionBlock) {
        this(world, pos, dir, retracted, extensionBlock, DEFAULT_MAX_MOVABLE_BLOCKS);
    }

    public ConfigurableLongPistonHandler(Level world, BlockPos pos, Direction dir, boolean retract,
                                         BasicMovingBlock extensionBlock, int maxMovableBlocks) {
        super(world, pos, dir, retract, maxMovableBlocks);
        this.extensionBlock = extensionBlock;
    }

    public boolean calculateLongPullPush(boolean isPull, Consumer<LongMovingBlockEntity> applySkip) {
        this.toMove.clear();
        this.toDestroy.clear();
        BlockState state = this.level.getBlockState(this.startPos);
        if (!PistonUtils.isMovable(state, this.level, this.startPos, this.moveDirection, false, this.pistonFacing)) {
            if (this.extend) {
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
            if (this.cantMoveWithBE(this.startPos, isPull ? this.moveDirection.getOpposite() : this.moveDirection))
                return false;
        }
        List<LongMovingBlockEntity> blockEntities = new ArrayList<>();
        for (int i = 0; i < this.toMove.size(); ++i) {
            BlockPos pos = this.toMove.get(i);
            state = this.level.getBlockState(pos);
            if (state.is(this.extensionBlock) &&
                    this.level.getBlockEntity(pos) instanceof LongMovingBlockEntity bpbe) {
                blockEntities.add(bpbe);
                state = bpbe.getMovedState();
            }
            ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
            if (stick.usesConfigurablePistonStickiness()) {
                if (stick.isSticky(state) && cantMoveAdjacentStickyBlocksWithBE(stick.stickySides(state), pos))
                    return false;
            } else {
                if (stick.hasStickyGroup() && this.cantMoveAdjacentBlocksWithBE(pos))
                    return false;
            }
        }
        for (LongMovingBlockEntity longPistonBE : blockEntities) {
            applySkip.accept(longPistonBE);
        }
        return true;
    }

    protected boolean cantMoveAdjacentBlocksWithBE(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        if (state.is(this.extensionBlock) && this.level.getBlockEntity(pos) instanceof LongMovingBlockEntity bpbe)
            state = bpbe.getMovedState();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.moveDirection.getAxis()) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState2 = this.level.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, state, blockState2) && this.cantMoveWithBE(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveAdjacentStickyBlocksWithBE(Map<Direction, StickyType> sides, BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        if (state.is(this.extensionBlock) && this.level.getBlockEntity(pos) instanceof LongMovingBlockEntity bpbe)
            state = bpbe.getMovedState();
        for (Map.Entry<Direction,StickyType> sideData : sides.entrySet()) {
            StickyType stickyType = sideData.getValue();
            if (stickyType == StickyType.NO_STICK) continue;
            Direction direction = sideData.getKey();
            if (direction.getAxis() != this.moveDirection.getAxis()) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState2 = this.level.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, state, blockState2) && this.cantMoveWithBE(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveWithBE(BlockPos pos, Direction dir) {
        BlockState state = this.level.getBlockState(pos);
        if (state.isAir() || pos.equals(this.pistonPos) || this.toMove.contains(pos)) return false;
        boolean isExtensionBlock = state.is(this.extensionBlock);
        LongMovingBlockEntity blockEntity = null;
        if (isExtensionBlock) {
            if (this.level.getBlockEntity(pos) instanceof LongMovingBlockEntity bpbe) {
                blockEntity = bpbe;
            } else {
                isExtensionBlock = false;
            }
        }
        if (!isExtensionBlock &&
                !PistonUtils.isMovable(state, this.level, pos, this.moveDirection, false, dir))
            return false;
        int i = 1;
        if (i + this.toMove.size() > this.maxMovableBlocks) return true;
        Direction dir2 = this.moveDirection.getOpposite();
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)(isExtensionBlock ?
                blockEntity.getMovedState().getBlock() : state.getBlock());
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, dir2).ordinal() >= StickyType.STICKY.ordinal()) :
                stick.hasStickyGroup();
        BlockPos blockPos = pos.relative(dir2, 0);
        while (isSticky) {
            boolean wasExtensionBlock = isExtensionBlock;
            BlockPos lastPos = blockPos;
            blockPos = pos.relative(dir2, i);
            BlockState oldState = state;
            state = this.level.getBlockState(blockPos);
            if (state.isAir()) break;
            isExtensionBlock = state.is(this.extensionBlock);
            if (isExtensionBlock) {
                if (this.level.getBlockEntity(pos) instanceof LongMovingBlockEntity bpbe) {
                    blockEntity = bpbe;
                } else {
                    isExtensionBlock = false;
                }
            }
            if (!(isExtensionBlock && wasExtensionBlock &&
                    PistonUtils.areExtensionsMatching(this.level, oldState, state, lastPos, blockPos))) {
                if (++i + this.toMove.size() > this.maxMovableBlocks) return true;
                continue;
            }
            if (isExtensionBlock) state = blockEntity.getMovedState();
            stick = (ConfigurablePistonStickiness)state.getBlock();
            if (!canAdjacentBlockStick(dir2, oldState, state) ||
                    blockPos.equals(this.pistonPos) ||
                    !PistonUtils.isMovable(state, this.level, blockPos, this.moveDirection, false, dir2))
                break;
            if (++i + this.toMove.size() > this.maxMovableBlocks) return true;
            if (stick.usesConfigurablePistonStickiness()) {
                boolean stickyStick = stick.isSticky(state);
                if (stickyStick && stick.sideStickiness(state, dir2).ordinal() < StickyType.STICKY.ordinal()) break;
                isSticky = stickyStick;
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
                    if (state.is(this.extensionBlock) &&
                            this.level.getBlockEntity(pos3) instanceof LongMovingBlockEntity bpbe) {
                        state = bpbe.getMovedState();
                    }
                    stick = (ConfigurablePistonStickiness)state.getBlock();
                    if (stick.usesConfigurablePistonStickiness()) {
                        if (stick.isSticky(state) && this.cantMoveAdjacentStickyBlocksWithBE(stick.stickySides(state),pos3))
                            return true;
                    } else {
                        if (stick.hasStickyGroup() && this.cantMoveAdjacentBlocksWithBE(pos3))
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
}
