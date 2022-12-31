package ca.fxco.configurablepistons.pistonLogic;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import static net.minecraft.block.Block.dropStacks;
import static net.minecraft.state.property.Properties.EXTENDED;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class PistonUtils {

    public static boolean isMovable(BlockState state, World wo, BlockPos pos,
                                    Direction dir, boolean canBreak, Direction pistonDir) {
        if (pos.getY() >= wo.getBottomY() && pos.getY() <= wo.getTopY() - 1 && wo.getWorldBorder().contains(pos)) {
            if (state.isAir()) return true;
            if (dir == DOWN && pos.getY() == wo.getBottomY()) return false;
            if (dir == UP && pos.getY() == wo.getTopY() - 1) return false;
            ConfigurablePistonBehavior customBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (customBehavior.usesConfigurablePistonBehavior()) { // This is where stuff gets fun
                if (customBehavior.isMovable(state))
                    return dir != pistonDir ? customBehavior.canPistonPull(state, dir) :
                            customBehavior.canPistonPush(state, dir) &&
                                    (!customBehavior.canDestroy(state) || canBreak);
            } else {
                if (state.isIn(ModTags.UNPUSHABLE) || state.getHardness(wo, pos) == -1.0F) return false;
                if (state.isIn(ModTags.PISTONS)) return !state.get(EXTENDED) && !state.hasBlockEntity();
                return switch (state.getPistonBehavior()) {
                    case BLOCK -> false;
                    case DESTROY -> canBreak;
                    case PUSH_ONLY -> dir == pistonDir;
                    default -> !state.hasBlockEntity();
                };
            }
        }
        return false;
    }

    public static boolean move(World world, BlockPos pos1, BasicPistonBlock piston, Direction dir,
                               boolean push, BiPredicate<ConfigurablePistonHandler,Boolean> pistonHandlerAction) {
        BlockPos pos2 = pos1.offset(dir);
        if (!push && world.getBlockState(pos2).isOf(piston.getHeadBlock()))
            world.setBlockState(pos2, Blocks.AIR.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
        ConfigurablePistonHandler pistonHandler = piston.getPistonHandler(world, pos1, dir, push);
        if (!pistonHandlerAction.test(pistonHandler,!push)) return false;
        Map<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = pistonHandler.getMovedBlocks();
        List<BlockState> list2 = Lists.newArrayList();
        for (BlockPos value : list) {
            BlockState blockState = world.getBlockState(value);
            list2.add(blockState);
            map.put(value, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = push ? dir : dir.getOpposite();
        int j = 0, k;
        BlockPos pos3;
        BlockState state2;
        for(k = list3.size() - 1; k >= 0; --k) {
            pos3 = list3.get(k);
            state2 = world.getBlockState(pos3);
            BlockEntity blockEntity = state2.hasBlockEntity() ? world.getBlockEntity(pos3) : null;
            dropStacks(state2, world, pos3, blockEntity);
            world.setBlockState(pos3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            if (!state2.isIn(BlockTags.FIRE)) world.addBlockBreakParticles(pos3, state2);
            blockStates[j++] = state2;
        }
        for(k = list.size() - 1; k >= 0; --k) {
            pos3 = list.get(k);
            state2 = world.getBlockState(pos3);
            pos3 = pos3.offset(direction);
            map.remove(pos3);
            BlockState state3 = piston.getExtensionBlock().getDefaultState().with(Properties.FACING, dir);
            world.setBlockState(pos3, state3, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(piston.getExtensionBlock().createPistonBlockEntity(
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
            PistonType pistonType = piston.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState state4 = piston.getHeadBlock().getDefaultState().with(PistonHeadBlock.FACING, dir)
                    .with(PistonHeadBlock.TYPE, pistonType);
            state2 = piston.getExtensionBlock().getDefaultState().with(PistonExtensionBlock.FACING, dir)
                    .with(PistonExtensionBlock.TYPE, piston.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(pos2);
            world.setBlockState(pos2, state2, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(piston.getExtensionBlock().createPistonBlockEntity(
                    pos2,
                    state2,
                    state4,
                    dir,
                    true,
                    true
            ));
        }
        BlockState blockState5 = Blocks.AIR.getDefaultState();
        for (BlockPos pos4 : map.keySet())
            world.setBlockState(pos4, blockState5, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
        BlockPos pos5;
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            pos5 = entry.getKey();
            entry.getValue().prepare(world, pos5, 2);
            blockState5.updateNeighbors(world, pos5, Block.NOTIFY_LISTENERS);
            blockState5.prepare(world, pos5, 2);
        }
        j = 0;
        for(k = list3.size() - 1; k >= 0; --k) {
            state2 = blockStates[j++];
            pos5 = list3.get(k);
            state2.prepare(world, pos5, 2);
            world.updateNeighborsAlways(pos5, state2.getBlock());
        }
        for(k = list.size() - 1; k >= 0; --k) world.updateNeighborsAlways(list.get(k), blockStates[j++].getBlock());
        if (push) world.updateNeighborsAlways(pos2, piston.getHeadBlock());
        return true;
    }

    // Does the state checks for you, although matches any moving piston instead of specific ones
    public static boolean areExtensionsMatching(World world, BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = world.getBlockState(pos2);
        if (!state1.isIn(ModTags.MOVING_PISTONS)) return false;
        BlockState state2 = world.getBlockState(pos2);
        if (!state2.isIn(ModTags.MOVING_PISTONS)) return false;
        return areExtensionsMatching(world, state1, state2, pos1, pos2);
    }

    // Does the state checks for you, with a specific extension block
    public static boolean areExtensionsMatching(World world, BasicPistonExtensionBlock extensionBlock,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = world.getBlockState(pos2);
        if (!state1.isOf(extensionBlock)) return false;
        BlockState state2 = world.getBlockState(pos2);
        if (!state2.isOf(extensionBlock)) return false;
        return areExtensionsMatching(world, state1, state2, pos1, pos2);
    }

    // You are expected to make sure that both states are of the correct extension, yourself
    public static boolean areExtensionsMatching(World world, BlockState state1, BlockState state2,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        if (state1.get(Properties.FACING) != state2.get(Properties.FACING) ||
                state1.get(Properties.PISTON_TYPE) != state2.get(Properties.PISTON_TYPE))
            return false;
        if (!(world.getBlockEntity(pos1) instanceof BasicPistonBlockEntity bpbe1) ||
                !(world.getBlockEntity(pos2) instanceof BasicPistonBlockEntity bpbe2))
            return false;
        return bpbe1.extending == bpbe2.extending && bpbe1.progress == bpbe2.progress && bpbe1.facing == bpbe2.facing;
    }
}
