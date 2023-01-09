package ca.fxco.configurablepistons.pistonLogic;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PistonUtils {

    // Does the state checks for you, although matches any moving piston instead of specific ones
    public static boolean areExtensionsMatching(Level world, BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = world.getBlockState(pos2);
        if (!state1.is(ModTags.MOVING_PISTONS)) return false;
        BlockState state2 = world.getBlockState(pos2);
        if (!state2.is(ModTags.MOVING_PISTONS)) return false;
        return areExtensionsMatching(world, state1, state2, pos1, pos2);
    }

    // Does the state checks for you, with a specific extension block
    public static boolean areExtensionsMatching(Level world, BasicMovingBlock movingBlock,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        BlockState state1 = world.getBlockState(pos2);
        if (!state1.is(movingBlock)) return false;
        BlockState state2 = world.getBlockState(pos2);
        if (!state2.is(movingBlock)) return false;
        return areExtensionsMatching(world, state1, state2, pos1, pos2);
    }

    // You are expected to make sure that both states are of the correct extension, yourself
    public static boolean areExtensionsMatching(Level world, BlockState state1, BlockState state2,
                                                BlockPos pos1, BlockPos pos2) {
        // Make sure they are moving pistons
        if (state1.getValue(BlockStateProperties.FACING) != state2.getValue(BlockStateProperties.FACING) ||
                state1.getValue(BlockStateProperties.PISTON_TYPE) != state2.getValue(BlockStateProperties.PISTON_TYPE))
            return false;
        if (!(world.getBlockEntity(pos1) instanceof BasicMovingBlockEntity bpbe1) ||
                !(world.getBlockEntity(pos2) instanceof BasicMovingBlockEntity bpbe2))
            return false;
        return bpbe1.extending == bpbe2.extending && bpbe1.progress == bpbe2.progress && bpbe1.direction == bpbe2.direction;
    }
}
