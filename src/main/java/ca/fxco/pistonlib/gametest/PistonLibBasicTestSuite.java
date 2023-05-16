package ca.fxco.pistonlib.gametest;

import ca.fxco.pistonlib.base.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class PistonLibBasicTestSuite {

    // Make sure pistons can push 12 blocks
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
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
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void dontPushUp13(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 12; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.DIRT);
        }
        helper.setBlock(0, 14, 0, Blocks.DIAMOND_BLOCK);
        helper.setBlock(0, 1, 1, Blocks.REDSTONE_BLOCK);

        GametestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 15, 0));
            return state.getBlock() == Blocks.AIR;
        }, "Piston is able to push 13 blocks");
    }

    // Make sure sticky pistons can push and pull 12 blocks
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void pushAndPull12(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_STICKY_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 11; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.SLIME_BLOCK);
        }
        helper.setBlock(0, 13, 0, Blocks.DIAMOND_BLOCK);
        helper.pulseRedstone(new BlockPos(0, 1, 1), 3);

        GametestUtil.succeedAfterDelay(helper, 6, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 13, 0));
            return state.getBlock() == Blocks.DIAMOND_BLOCK;
        }, "Piston is unable to push 12 blocks");
    }

    // Make sure Honey does not stick to Slime
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void slimeAndHoney(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        for (int i = 0; i < 12; i++) {
            helper.setBlock(0, 2 + i, 0, Blocks.SLIME_BLOCK);
            helper.setBlock(0, 2 + i, -1, Blocks.HONEY_BLOCK);
        }
        helper.setBlock(0, 1, 1, Blocks.REDSTONE_BLOCK);

        GametestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 14, -1));
            return state.getBlock() == Blocks.AIR;
        }, "Honey is sticking to Slime!");
    }

    // Make sure obsidian is still immovable
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void immovableObsidian(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        helper.setBlock(0, 2, 0, Blocks.OBSIDIAN);
        helper.setBlock(0, 1, 1, Blocks.REDSTONE_BLOCK);

        GametestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 3, 0));
            return state.getBlock() != Blocks.OBSIDIAN;
        }, "Obsidian was moved!");
    }

    // Check if 2 game tick pulses still keep waterlogged state
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void pushWaterDirectly(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_STICKY_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        helper.setBlock(0, 2, 0, Blocks.CHAIN.defaultBlockState().setValue(WATERLOGGED, true));
        helper.pulseRedstone(new BlockPos(0, 1, 1), 2);

        GametestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(0, 3, 0));
            return state.hasProperty(WATERLOGGED) && state.getValue(WATERLOGGED);
        }, "2 gametick pulse did not maintain the chains waterlogged state!");
    }

    // Make sure 2 gametick pulses only push water when directly in front of the piston
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void dontPushWaterOffset(GameTestHelper helper) {
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_STICKY_PISTON.defaultBlockState().setValue(FACING, Direction.UP));
        helper.setBlock(0, 2, 0, Blocks.SLIME_BLOCK);
        helper.setBlock(1, 2, 0, Blocks.CHAIN.defaultBlockState().setValue(WATERLOGGED, true));
        helper.pulseRedstone(new BlockPos(0, 1, 1), 2);

        GametestUtil.succeedAfterDelay(helper, 3, () -> {
            BlockState state = helper.getBlockState(new BlockPos(1, 3, 0));
            return state.hasProperty(WATERLOGGED) && !state.getValue(WATERLOGGED);
        }, "2 gametick pulse maintained the chains waterlogged state when indirectly pushed!");
    }

    // Piston should break bedrock if headless
   /* @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 3)
    public void headlessPistonIllegalBreak(GameTestHelper helper) {
        helper.setBlock(0, 2, 0, Blocks.BEDROCK);
        // TODO: Set extended piston body without updates
        helper.setBlock(0, 1, 0, ModBlocks.BASIC_PISTON.defaultBlockState().setValue(FACING, Direction.UP).setValue(EXTENDED, true));
        helper.setBlock(0, 1, 1, Blocks.STONE);

        helper.succeedWhenBlockPresent(Blocks.AIR, 0, 2, 0);
    }*/
}
