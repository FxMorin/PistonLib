package ca.fxco.configurablepistons.pistonLogic.pistonHandlers;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.pistons.longPiston.LongPistonBlockEntity;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigurableLongPistonHandler extends ConfigurablePistonHandler {

    protected final BasicPistonExtensionBlock extensionBlock;

    public ConfigurableLongPistonHandler(World world, BlockPos pos, Direction dir,
                                         boolean retracted, BasicPistonExtensionBlock extensionBlock) {
        this(world, pos, dir, retracted, extensionBlock, DEFAULT_MAX_MOVABLE_BLOCKS);
    }

    public ConfigurableLongPistonHandler(World world, BlockPos pos, Direction dir, boolean retract,
                                         BasicPistonExtensionBlock extensionBlock, int maxMovableBlocks) {
        super(world, pos, dir, retract, maxMovableBlocks);
        this.extensionBlock = extensionBlock;
    }

    public boolean calculateLongPullPush(boolean isPull, Consumer<LongPistonBlockEntity> applySkip) {
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
            if (this.cantMoveWithBE(this.posTo, isPull ? this.motionDirection.getOpposite() : this.motionDirection))
                return false;
        }
        List<LongPistonBlockEntity> blockEntities = new ArrayList<>();
        for (BlockPos pos : this.movedBlocks) {
            state = this.world.getBlockState(pos);
            if (state.isOf(this.extensionBlock) &&
                    this.world.getBlockEntity(pos) instanceof LongPistonBlockEntity bpbe) {
                blockEntities.add(bpbe);
                state = bpbe.getPushedBlock();
            }
            ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) state.getBlock();
            if (stick.usesConfigurablePistonStickiness()) {
                if (stick.isSticky(state) && cantMoveAdjacentStickyBlocksWithBE(stick.stickySides(state), pos))
                    return false;
            } else {
                if (isBlockSticky(this.world.getBlockState(pos)) && this.cantMoveAdjacentBlocksWithBE(pos))
                    return false;
            }
        }
        for (LongPistonBlockEntity longPistonBE : blockEntities) {
            applySkip.accept(longPistonBE);
        }
        return true;
    }

    protected boolean cantMoveAdjacentBlocksWithBE(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        if (state.isOf(this.extensionBlock) && this.world.getBlockEntity(pos) instanceof LongPistonBlockEntity bpbe)
            state = bpbe.getPushedBlock();
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, state, blockState2) && this.cantMoveWithBE(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveAdjacentStickyBlocksWithBE(Map<Direction, StickyType> sides, BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        if (state.isOf(this.extensionBlock) && this.world.getBlockEntity(pos) instanceof LongPistonBlockEntity bpbe)
            state = bpbe.getPushedBlock();
        for (Map.Entry<Direction,StickyType> sideData : sides.entrySet()) {
            StickyType stickyType = sideData.getValue();
            if (stickyType == StickyType.NO_STICK) continue;
            Direction direction = sideData.getKey();
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);
                if (canAdjacentBlockStick(direction, state, blockState2) && this.cantMoveWithBE(blockPos, direction))
                    return true;
            }
        }
        return false;
    }

    protected boolean cantMoveWithBE(BlockPos pos, Direction dir) {
        BlockState state = this.world.getBlockState(pos);
        if (state.isAir() || pos.equals(this.posFrom) || this.movedBlocks.contains(pos)) return false;
        boolean isExtensionBlock = state.isOf(this.extensionBlock);
        LongPistonBlockEntity blockEntity = null;
        if (isExtensionBlock) {
            if (this.world.getBlockEntity(pos) instanceof LongPistonBlockEntity bpbe) {
                blockEntity = bpbe;
            } else {
                isExtensionBlock = false;
            }
        }
        if (!isExtensionBlock &&
                !PistonUtils.isMovable(state, this.world, pos, this.motionDirection, false, dir))
            return false;
        int i = 1;
        if (i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
        Direction dir2 = this.motionDirection.getOpposite();
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)(isExtensionBlock ?
                blockEntity.getPushedBlock().getBlock() : state.getBlock());
        boolean isSticky = stick.usesConfigurablePistonStickiness() ?
                (stick.isSticky(state) && stick.sideStickiness(state, dir2).ordinal() >= StickyType.STICKY.ordinal()) :
                isBlockSticky(state);
        BlockPos blockPos = pos.offset(dir2, 0);
        while (isSticky) {
            boolean wasExtensionBlock = isExtensionBlock;
            BlockPos lastPos = blockPos;
            blockPos = pos.offset(dir2, i);
            BlockState oldState = state;
            state = this.world.getBlockState(blockPos);
            if (state.isAir()) break;
            isExtensionBlock = state.isOf(this.extensionBlock);
            if (isExtensionBlock) {
                if (this.world.getBlockEntity(pos) instanceof LongPistonBlockEntity bpbe) {
                    blockEntity = bpbe;
                } else {
                    isExtensionBlock = false;
                }
            }
            if (!(isExtensionBlock && wasExtensionBlock &&
                    PistonUtils.areExtensionsMatching(this.world, oldState, state, lastPos, blockPos))) {
                if (++i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
                continue;
            }
            if (isExtensionBlock) state = blockEntity.getPushedBlock();
            stick = (ConfigurablePistonStickiness)state.getBlock();
            if (!canAdjacentBlockStick(dir2, oldState, state) ||
                    blockPos.equals(this.posFrom) ||
                    !PistonUtils.isMovable(state, this.world, blockPos, this.motionDirection, false, dir2))
                break;
            if (++i + this.movedBlocks.size() > this.maxMovableBlocks) return true;
            if (stick.usesConfigurablePistonStickiness()) {
                boolean stickyStick = stick.isSticky(state);
                if (stickyStick && stick.sideStickiness(state, dir2).ordinal() < StickyType.STICKY.ordinal()) break;
                isSticky = stickyStick;
            } else {
                isSticky = isBlockSticky(state);
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
                    if (state.isOf(this.extensionBlock) &&
                            this.world.getBlockEntity(pos3) instanceof LongPistonBlockEntity bpbe) {
                        state = bpbe.getPushedBlock();
                    }
                    stick = (ConfigurablePistonStickiness)state.getBlock();
                    if (stick.usesConfigurablePistonStickiness()) {
                        if (stick.isSticky(state) && this.cantMoveAdjacentStickyBlocksWithBE(stick.stickySides(state),pos3))
                            return true;
                    } else {
                        if (isBlockSticky(state) && this.cantMoveAdjacentBlocksWithBE(pos3))
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
}
