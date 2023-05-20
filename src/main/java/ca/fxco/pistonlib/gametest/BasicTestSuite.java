package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class BasicTestSuite {

    // Make sure pistons can push 12 blocks
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 3)
    public void pushUp12(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 11; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.DIRT);
        }
        helper.setBlock(0, 13, 0, Blocks.DIAMOND_BLOCK);
        helper.setBlock(0, 1, 1, Blocks.REDSTONE_BLOCK);

        helper.succeedWhenBlockPresent(Blocks.DIAMOND_BLOCK, 0, 14, 0);
    }

    // Make sure pistons cant push 13 blocks
    @GameTest(timeoutTicks = 4)
    public void pushlimit(GameTestHelper helper) {
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
    @GameTest(timeoutTicks = 5)
    public void waterPushDirectly(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Make sure 2 gametick pulses only push water when directly in front of the piston
    @GameTest(timeoutTicks = 5)
    public void waterPushInDirectly(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    // Piston should break bedrock if headless
    @GameTest(timeoutTicks = 4)
    public void headlessPistonIllegalBreak(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest(timeoutTicks = 90)
    public void strongPull(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest(timeoutTicks = 90)
    public void strongPush(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest(timeoutTicks = 6)
    public void zerotick(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest(timeoutTicks = 6) // Change timeout to: 3
    public void mergingslabs(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest(timeoutTicks = 7)
    public void headRetractionUpdate(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }
}
