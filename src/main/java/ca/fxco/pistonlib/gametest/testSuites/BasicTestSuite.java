package ca.fxco.pistonlib.gametest.testSuites;

import ca.fxco.gametestlib.gametest.GameTestUtil;
import ca.fxco.gametestlib.gametest.expansion.GameTestConfig;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class BasicTestSuite {

    // Make sure pistons can push 12 blocks
    @GameTest(timeoutTicks = 4)
    public void push12(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Make sure pistons cant push 13 blocks
    @GameTest(timeoutTicks = 4)
    public void pushLimit(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Tests the piston pushing 12 blocks than only pulling 11 back
    @GameTest(timeoutTicks = 7)
    public void pushAndDrop(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Make sure Honey does not stick to Slime
    @GameTest(timeoutTicks = 5)
    public void slimeAndHoney(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Make sure obsidian is still immovable
    @GameTest(timeoutTicks = 4)
    public void immovable(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Check if 2 game tick pulses still keep waterlogged state
    @GameTestConfig(value = "pistonsPushWaterloggedBlocks")
    @GameTest(timeoutTicks = 5)
    public void waterPushDirectly(GameTestHelper helper) {}

    // Make sure 2 game tick pulses only push water when directly in front of the piston
    @GameTestConfig(value = "pistonsPushWaterloggedBlocks")
    @GameTest(timeoutTicks = 5)
    public void waterPushInDirectly(GameTestHelper helper) {}

    // Piston should break bedrock if headless
    @GameTestConfig(value = "illegalBreakingFix")
    @GameTest(timeoutTicks = 4)
    public void headlessPistonIllegalBreak(GameTestHelper helper) {}

    // Check if the strong piston can still pull 24 blocks
    @GameTest(timeoutTicks = 90)
    public void strongPull(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Check if the strong piston can still push 24 blocks
    @GameTest(timeoutTicks = 90)
    public void strongPush(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Make sure 0-ticks still work with!
    @GameTest(timeoutTicks = 6)
    public void zerotick(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Check if a retracting sticky piston head is still missing an update if it fails to pull a structure
    @GameTest(timeoutTicks = 7)
    public void headRetractionUpdate(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }
}
