package ca.fxco.pistonlib.pistonLogic;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PistonUtils {

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
