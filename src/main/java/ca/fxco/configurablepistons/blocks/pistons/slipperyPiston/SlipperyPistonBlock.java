package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
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

public class SlipperyPistonBlock extends BasicPistonBlock {

    public SlipperyPistonBlock(boolean isSticky) {
        super(isSticky);
        this.setDefaultState(this.stateManager.getDefaultState().with(SLIPPERY_DISTANCE, MAX_DISTANCE));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
            world.createAndScheduleBlockTick(pos, this, DELAY);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(EXTENDED, false)
                .with(SLIPPERY_DISTANCE, AbstractSlipperyBlock.calculateDistance(ctx.getWorld(), ctx.getBlockPos()));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) {
            if (neighborState.isIn(ModTags.SLIPPERY_BLOCKS)) {
                world.createAndScheduleBlockTick(pos, this, SLIP_DELAY);
            } else {
                world.createAndScheduleBlockTick(pos, this, DELAY);
            }
        }
        return state;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = AbstractSlipperyBlock.calculateDistance(world, pos);
        BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
        if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
            FallingBlockEntity.spawnFromBlock(world, pos, blockState.with(EXTENDED,false));
        }// else if (state != blockState) {
            //world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        //}
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED, SLIPPERY_DISTANCE);
    }
}
