package ca.fxco.pistonlib.gametest.extension;

import ca.fxco.pistonlib.blocks.gametest.CheckStateBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameTestGroupConditions {

    private final List<TestCondition> testConditions = new ArrayList<>();

    public void runTick(GameTestHelper helper) {
        for (TestCondition testCondition : testConditions) {
            if (testCondition.getCheckStateBe().getTick() > -1) {
                if (testCondition.getCheckStateBe().getTick() == helper.getTick()) {
                    if (!testCondition.runCheck(helper)) {
                        break;
                    } else {
                        testCondition.setSuccess(true);
                    }
                }
            } else {
                Boolean result = testCondition.runCheck(helper);
                if (result != null) {
                    if (!result) {
                        break;
                    } else {
                        testCondition.setSuccess(true);
                    }
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
        testConditions.add(new TestCondition(checkStateBe, checkPos));
    }

    public static class TestCondition {

        @Getter
        @Setter
        private boolean success = false;
        private final BlockPos checkPos;
        @Getter
        private final CheckStateBlockEntity checkStateBe;

        public TestCondition(CheckStateBlockEntity checkStateBe, BlockPos checkPos) {
            this.checkStateBe = checkStateBe;
            this.checkPos = checkPos;
        }

        public Boolean runCheck(GameTestHelper helper) {
            return checkStateBe.runGameTestChecks(helper, checkPos);
        }
    }
}
