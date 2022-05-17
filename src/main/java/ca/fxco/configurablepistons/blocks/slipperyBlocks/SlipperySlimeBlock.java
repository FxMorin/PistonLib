package ca.fxco.configurablepistons.blocks.slipperyBlocks;

import ca.fxco.configurablepistons.base.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock.*;

// Inherits from SlimeBlock instead of AbstractSlipperyBlock so that it won't stick to honey
public class SlipperySlimeBlock extends SlimeBlock {

    public static final IntProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    public SlipperySlimeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SLIPPERY_DISTANCE, 0));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(SLIPPERY_DISTANCE, calculateDistance(ctx.getWorld(), ctx.getBlockPos()));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
        return state;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = calculateDistance(world, pos);
        BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
        if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
            FallingBlockEntity.spawnFromBlock(world, pos, blockState);
        } else if (state != blockState) {
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return calculateDistance(world, pos) < MAX_DISTANCE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SLIPPERY_DISTANCE);
    }
}
