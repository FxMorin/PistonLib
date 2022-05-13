package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.PistonType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

import static ca.fxco.configurablepistons.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock.*;

public class SlipperyPistonHeadBlock extends BasicPistonHeadBlock {

    public SlipperyPistonHeadBlock() {
        super();
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, PistonType.DEFAULT)
                .with(SHORT, false)
                .with(SLIPPERY_DISTANCE, MAX_DISTANCE));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null)
            world.createAndScheduleBlockTick(pos, this, DELAY);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) {
            if (neighborState.isIn(ModTags.SLIPPERY_BLOCKS)) {
                world.createAndScheduleBlockTick(pos, this, SLIP_DELAY);
            } else {
                world.createAndScheduleBlockTick(pos, this, DELAY);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = AbstractSlipperyBlock.calculateDistance(world, pos);
        BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
        if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
            world.removeBlock(pos,true);
        } else if (state != blockState) {
            //world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE ||
                super.canPlaceAt(state, world, pos);
    }

    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT, SLIPPERY_DISTANCE);
    }
}
