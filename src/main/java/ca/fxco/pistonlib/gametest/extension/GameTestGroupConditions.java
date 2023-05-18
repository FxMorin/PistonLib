package ca.fxco.pistonlib.gametest.extension;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.blocks.gametest.CheckStateBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameTestGroupConditions {

    private final List<TestCondition> testConditions = new ArrayList<>();

    public void runTick(GameTestHelper helper) {
        for (TestCondition testCondition : testConditions) {
            if (testCondition.isSingleTick()) {
                if (testCondition.canRunThisTick(helper.getTick())) {
                    if (!testCondition.runCheck(helper)) {
                        break;
                    }
                    testCondition.setSuccess(true);
                }
            } else {
                Boolean result = testCondition.runCheck(helper);
                if (result != null) {
                    if (!result) {
                        break;
                    }
                    testCondition.setSuccess(true);
                }
            }
        }
    }

    public boolean isSuccess() {
        for (TestCondition testCondition : testConditions) {
            if (!testCondition.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    public void addCondition(CheckStateBlockEntity checkStateBe, BlockPos checkPos) {
        testConditions.add(new CheckStateTestCondition(checkStateBe, checkPos));
    }

    public void addTestTrigger(BlockPos blockPos) {
        testConditions.add(new TriggerTestCondition(blockPos));
    }

    public static abstract class TestCondition {
        public abstract boolean isSuccess();
        public abstract void setSuccess(boolean state);
        public abstract boolean isSingleTick();
        public boolean canRunThisTick(long tick) {return false;}
        public abstract Boolean runCheck(GameTestHelper helper);
    }

    public static class TriggerTestCondition extends TestCondition {

        @Getter
        @Setter
        private boolean success = false;
        private final BlockPos blockPos;

        public TriggerTestCondition(BlockPos blockPos) {
            this.blockPos = blockPos.immutable();
        }

        @Override
        public boolean isSingleTick() {
            return false;
        }

        @Override
        public Boolean runCheck(GameTestHelper helper) {
            BlockState state = helper.getBlockState(blockPos);
            if (state.getBlock() == ModBlocks.TEST_TRIGGER_BLOCK) {
                if (state.getValue(BlockStateProperties.POWERED)) {
                    if (state.getValue(BlockStateProperties.INVERTED)) {
                        helper.fail("Test Trigger at " + blockPos.toShortString() + " was triggered");
                        return false;
                    }
                    return true;
                }
                return null;
            }
            return false;
        }
    }

    public static class CheckStateTestCondition extends TestCondition {

        @Getter
        @Setter
        private boolean success = false;
        private final BlockPos checkPos;
        @Getter
        private final CheckStateBlockEntity checkStateBe;

        public CheckStateTestCondition(CheckStateBlockEntity checkStateBe, BlockPos checkPos) {
            this.checkStateBe = checkStateBe;
            this.checkPos = checkPos.immutable();
        }

        @Override
        public boolean isSingleTick() {
            return this.checkStateBe.getTick() > -1;
        }

        @Override
        public boolean canRunThisTick(long tick) {
            return this.checkStateBe.getTick() == tick;
        }

        @Override
        public Boolean runCheck(GameTestHelper helper) {
            return checkStateBe.runGameTestChecks(helper, checkPos);
        }
    }
}
