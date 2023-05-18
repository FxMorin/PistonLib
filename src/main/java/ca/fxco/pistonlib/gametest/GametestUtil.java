package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.gametest.CheckStateBlockEntity;
import ca.fxco.pistonlib.blocks.gametest.PulseStateBlockEntity;
import ca.fxco.pistonlib.gametest.extension.GameTestGroupConditions;
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
        if (helper.getTick() != 0) { // Only run searching logic on the first tick
            return;
        }
        GameTestGroupConditions groupConditions = new GameTestGroupConditions();
        helper.forEveryBlockInStructure(blockPos -> {
            BlockState state = helper.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block == ModBlocks.PULSE_STATE_BLOCK) {
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
            } else if (block == ModBlocks.CHECK_STATE_BLOCK) {
                BlockEntity blockEntity = helper.getBlockEntity(blockPos);
                if (blockEntity instanceof CheckStateBlockEntity checkStateBe) {
                    BlockPos checkPos = blockPos.relative(checkStateBe.getDirection());
                    groupConditions.addCondition(checkStateBe, checkPos);
                }
            } else if (block == ModBlocks.TEST_TRIGGER_BLOCK) {
                groupConditions.addTestTrigger(blockPos);
            }
        });
        if (!groupConditions.getTestConditions().isEmpty()) {
            helper.onEachTick(() -> {
                groupConditions.runTick(helper);
                if (groupConditions.isSuccess()) {
                    helper.succeed();
                }
            });
        }
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
