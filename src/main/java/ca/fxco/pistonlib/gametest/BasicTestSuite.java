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

    // Make sure sticky pistons can push and pull 12 blocks
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 7)
    public void pushAndPull12(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_STICKY_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 11; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.SLIME_BLOCK);
        }
        helper.setBlock(0, 13, 0, Blocks.DIAMOND_BLOCK);
        helper.pulseRedstone(new BlockPos(0, 1, 1), 3);

        GameTestUtil.succeedAfterDelay(helper, 6, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 13, 0));
            return state.getBlock() == Blocks.DIAMOND_BLOCK;
        }, "Piston is unable to push 12 blocks");
    }

    // Make sure Honey does not stick to Slime
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 4)
    public void slimeAndHoney(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 12; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.SLIME_BLOCK);
            helper.setBlock(0, 2 + i, -1, Blocks.HONEY_BLOCK);
        }
        helper.setBlock(0, 1, 1, Blocks.REDSTONE_BLOCK);

        GameTestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 14, -1));
            return state.getBlock() == Blocks.AIR;
        }, "Honey is sticking to Slime!");
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
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 3)
    public void headlessPistonIllegalBreak(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.BEDROCK);
        GameTestUtil.setBlock(helper, 0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP).setValue(EXTENDED, true), Block.UPDATE_NONE);
        helper.setBlock(0, 1, 1, Blocks.STONE);

        helper.succeedWhenBlockPresent(Blocks.AIR, 0, 2, 0);
    }

    @GameTest
    public void strongPull(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest
    public void strongPush(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest
    public void zerotick(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest
    public void mergingslabs(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }

    @GameTest
    public void headRetractionUpdate(GameTestHelper helper) {
        GameTestUtil.pistonLibGameTest(helper);
    }
}
