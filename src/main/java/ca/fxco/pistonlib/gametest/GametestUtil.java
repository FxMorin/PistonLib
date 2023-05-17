package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.gametest.PulseStateBlockEntity;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BooleanSupplier;

public class GametestUtil {

    public static void succeedAfterDelay(GameTestHelper helper, long tick, BooleanSupplier supplier, String failReason) {
        helper.runAfterDelay(tick, () -> {
            if (supplier.getAsBoolean()) {
                helper.succeed();
            } else {
                helper.fail(failReason);
            }
        });
    }

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
}
