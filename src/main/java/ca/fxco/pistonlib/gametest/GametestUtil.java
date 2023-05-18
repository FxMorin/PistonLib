package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.gametest.PulseStateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BooleanSupplier;

public class GametestUtil {

    /**
     * By using this method as the first line in a gametest, you will be able to use gametest blocks within those tests
     * TODO: Add success & fail conditions
     */
    public static void pistonLibGameTest(GameTestHelper helper) {
        helper.forEveryBlockInStructure(blockPos -> {
            BlockState state = helper.getBlockState(blockPos);
            if (state.getBlock() == ModBlocks.PULSE_STATE_BLOCK) {
                BlockEntity blockEntity = helper.getBlockEntity(blockPos);
                if (blockEntity instanceof PulseStateBlockEntity pulseStateBe) {
                    helper.setBlock(blockPos, pulseStateBe.getFirstBlockState());
                    int delay = pulseStateBe.getDelay();
                    int duration = pulseStateBe.getDuration();
                    if (delay > 0) {
                        helper.runAfterDelay(delay, () -> helper.setBlock(blockPos, pulseStateBe.getPulseBlockState()));
                    }
                    if (duration > 0) {
                        helper.runAfterDelay(delay + duration, () -> helper.setBlock(blockPos, pulseStateBe.getLastBlockState()));
                    }
                }
            }
        });
    }

    public static void succeedAfterDelay(GameTestHelper helper, long tick, BooleanSupplier supplier, String failReason) {
        helper.runAfterDelay(tick, () -> {
            if (supplier.getAsBoolean()) {
                helper.succeed();
            } else {
                helper.fail(failReason);
            }
        });
    }

    public static void setBlock(GameTestHelper helper, int i, int j, int k, Block block, int flags) {
        setBlock(helper, new BlockPos(i, j, k), block, flags);
    }

    public static void setBlock(GameTestHelper helper, int i, int j, int k, BlockState blockState, int flags) {
        setBlock(helper, new BlockPos(i, j, k), blockState, flags);
    }

    public static void setBlock(GameTestHelper helper, BlockPos blockPos, Block block, int flags) {
        setBlock(helper, blockPos, block.defaultBlockState(), flags);
    }

    public static void setBlock(GameTestHelper helper, BlockPos blockPos, BlockState blockState, int flags) {
        helper.getLevel().setBlock(helper.absolutePos(blockPos), blockState, flags);
    }
}
