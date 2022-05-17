package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.AbstractSlipperyBlock.*;

public class SlipperyPistonBlock extends BasicPistonBlock {

    public SlipperyPistonBlock(boolean isSticky) {
        super(isSticky);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
            world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient())
            world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (AbstractSlipperyBlock.calculateDistance(world, pos) >= MAX_DISTANCE)
            FallingBlockEntity.spawnFromBlock(world, pos, state.with(EXTENDED,false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE;
    }
}
