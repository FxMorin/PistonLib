package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import ca.fxco.configurablepistons.base.ModProperties;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

import static ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock.EXTENDED;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class SlipperyPistonHeadBlock extends BasicPistonHeadBlock {

    public static final IntProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    public SlipperyPistonHeadBlock() {
        super();
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, PistonType.DEFAULT)
                .with(SHORT, false)
                .with(SLIPPERY_DISTANCE, 0));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null)
            world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient())
            world.createAndScheduleBlockTick(pos, this, SLIPPERY_DELAY);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = BaseSlipperyBlock.calculateDistance(world, pos);
        BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
        if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE && !super.canPlaceAt(state, world, pos)) {
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            if (this.isAttached(state, world.getBlockState(blockPos)))
                FallingBlockEntity.spawnFromBlock(world, blockPos, world.getBlockState(blockPos).with(EXTENDED,false));
            world.removeBlock(pos,false);
        } else if (state != blockState) {
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return BaseSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE ||
                super.canPlaceAt(state, world, pos);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT, SLIPPERY_DISTANCE);
    }
}
