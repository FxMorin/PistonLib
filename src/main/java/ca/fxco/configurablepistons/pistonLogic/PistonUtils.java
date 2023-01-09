package ca.fxco.configurablepistons.pistonLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.world.level.block.Block.dropResources;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EXTENDED;

public class PistonUtils {

    public static boolean isMovable(BlockState state, Level level, BlockPos pos, Direction moveDir,
                                    boolean allowDestroy, Direction pistonFacing) {
        if (pos.getY() >= level.getMinBuildHeight() && pos.getY() <= level.getMaxBuildHeight() - 1 && level.getWorldBorder().isWithinBounds(pos)) {
            if (state.isAir()) return true;
            if (moveDir == DOWN && pos.getY() == level.getMinBuildHeight()) return false;
            if (moveDir == UP && pos.getY() == level.getMaxBuildHeight() - 1) return false;
            ConfigurablePistonBehavior customBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (customBehavior.usesConfigurablePistonBehavior()) { // This is where stuff gets fun
                if (customBehavior.isMovable(state))
                    return moveDir != pistonFacing ? customBehavior.canPistonPull(state, moveDir) :
                            customBehavior.canPistonPush(state, moveDir) &&
                                    (!customBehavior.canDestroy(state) || allowDestroy);
            } else {
                if (state.is(ModTags.UNPUSHABLE) || state.getDestroySpeed(level, pos) == -1.0F) return false;
                if (state.is(ModTags.PISTONS)) return !state.getValue(EXTENDED) && !state.hasBlockEntity();
                return switch (state.getPistonPushReaction()) {
                    case BLOCK -> false;
                    case DESTROY -> allowDestroy;
                    case PUSH_ONLY -> moveDir == pistonFacing;
                    default -> !state.hasBlockEntity();
                };
            }
        }
        return false;
    }

    public static boolean move(Level level, BlockPos pos1, BasicPistonBaseBlock piston, Direction dir,
                               boolean push, BiPredicate<ConfigurablePistonStructureResolver,Boolean> pistonHandlerAction) {
        BlockPos pos2 = pos1.relative(dir);
        if (!push && level.getBlockState(pos2).is(piston.getHeadBlock()))
            level.setBlock(pos2, Blocks.AIR.defaultBlockState(), Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE);
        ConfigurablePistonStructureResolver pistonHandler = piston.createStructureResolver(level, pos1, dir, push);
        if (!pistonHandlerAction.test(pistonHandler,!push)) return false;
        Map<BlockPos, BlockState> map = new HashMap<>();
        List<BlockPos> list = pistonHandler.getToMove();
        List<BlockState> list2 = new ArrayList<>();
        for (BlockPos value : list) {
            BlockState blockState = level.getBlockState(value);
            list2.add(blockState);
            map.put(value, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getToDestroy();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = push ? dir : dir.getOpposite();
        int j = 0, k;
        BlockPos pos3;
        BlockState state2;
        for(k = list3.size() - 1; k >= 0; --k) {
            pos3 = list3.get(k);
            state2 = level.getBlockState(pos3);
            BlockEntity blockEntity = state2.hasBlockEntity() ? level.getBlockEntity(pos3) : null;
            dropResources(state2, level, pos3, blockEntity);
            level.setBlock(pos3, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            if (!state2.is(BlockTags.FIRE)) level.addDestroyBlockEffect(pos3, state2);
            blockStates[j++] = state2;
        }
        for(k = list.size() - 1; k >= 0; --k) {
            pos3 = list.get(k);
            state2 = level.getBlockState(pos3);
            pos3 = pos3.relative(direction);
            map.remove(pos3);
            BlockState state3 = piston.getMovingBlock().defaultBlockState().setValue(BlockStateProperties.FACING, dir);
            level.setBlock(pos3, state3, Block.UPDATE_IMMEDIATE | Block.UPDATE_MOVE_BY_PISTON);
            level.setBlockEntity(piston.getMovingBlock().createMovingBlockEntity(
                    pos3,
                    state3,
                    list2.get(k),
                    dir,
                    push,
                    false
            ));
            blockStates[j++] = state2;
        }
        if (push) {
            PistonType pistonType = piston.type;
            BlockState state4 = piston.getHeadBlock().defaultBlockState().setValue(PistonHeadBlock.FACING, dir)
                    .setValue(PistonHeadBlock.TYPE, pistonType);
            state2 = piston.getMovingBlock().defaultBlockState().setValue(MovingPistonBlock.FACING, dir)
                    .setValue(MovingPistonBlock.TYPE, piston.type);
            map.remove(pos2);
            level.setBlock(pos2, state2, Block.UPDATE_IMMEDIATE | Block.UPDATE_MOVE_BY_PISTON);
            level.setBlockEntity(piston.getMovingBlock().createMovingBlockEntity(
                    pos2,
                    state2,
                    state4,
                    dir,
                    true,
                    true
            ));
        }
        BlockState blockState5 = Blocks.AIR.defaultBlockState();
        for (BlockPos pos4 : map.keySet())
            level.setBlock(pos4, blockState5, Block.UPDATE_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_MOVE_BY_PISTON);
        BlockPos pos5;
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            pos5 = entry.getKey();
            entry.getValue().updateIndirectNeighbourShapes(level, pos5, 2);
            blockState5.updateNeighbourShapes(level, pos5, Block.UPDATE_CLIENTS);
            blockState5.updateIndirectNeighbourShapes(level, pos5, 2);
        }
        j = 0;
        for(k = list3.size() - 1; k >= 0; --k) {
            state2 = blockStates[j++];
            pos5 = list3.get(k);
            state2.updateIndirectNeighbourShapes(level, pos5, 2);
            level.updateNeighborsAt(pos5, state2.getBlock());
        }
        for(k = list.size() - 1; k >= 0; --k) level.updateNeighborsAt(list.get(k), blockStates[j++].getBlock());
        if (push) level.updateNeighborsAt(pos2, piston.getHeadBlock());
        return true;
    }

    // Does the state checks for you, although matches any moving piston instead of specific ones
    public static boolean areExtensionsMatching(Level level, BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = level.getBlockState(pos2);
        if (!state1.is(ModTags.MOVING_PISTONS)) return false;
        BlockState state2 = level.getBlockState(pos2);
        if (!state2.is(ModTags.MOVING_PISTONS)) return false;
        return areExtensionsMatching(level, state1, state2, pos1, pos2);
    }

    // Does the state checks for you, with a specific extension block
    public static boolean areExtensionsMatching(Level level, BasicMovingBlock movingBlock,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = level.getBlockState(pos2);
        if (!state1.is(movingBlock)) return false;
        BlockState state2 = level.getBlockState(pos2);
        if (!state2.is(movingBlock)) return false;
        return areExtensionsMatching(level, state1, state2, pos1, pos2);
    }

    // You are expected to make sure that both states are of the correct extension, yourself
    public static boolean areExtensionsMatching(Level level, BlockState state1, BlockState state2,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        if (state1.getValue(BlockStateProperties.FACING) != state2.getValue(BlockStateProperties.FACING) ||
                state1.getValue(BlockStateProperties.PISTON_TYPE) != state2.getValue(BlockStateProperties.PISTON_TYPE))
            return false;
        if (!(level.getBlockEntity(pos1) instanceof BasicMovingBlockEntity bpbe1) ||
                !(level.getBlockEntity(pos2) instanceof BasicMovingBlockEntity bpbe2))
            return false;
        return bpbe1.extending == bpbe2.extending && bpbe1.progress == bpbe2.progress && bpbe1.direction == bpbe2.direction;
    }
}
