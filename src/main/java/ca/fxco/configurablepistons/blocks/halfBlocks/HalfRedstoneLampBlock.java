package ca.fxco.configurablepistons.blocks.halfBlocks;

import ca.fxco.configurablepistons.helpers.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HalfRedstoneLampBlock extends RedstoneLampBlock {

    public static final DirectionProperty FACING = Properties.FACING;

    public HalfRedstoneLampBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(LIT);
            if (bl != Utils.isReceivingRedstonePowerExcludingDirection(world, pos, state.get(FACING).getOpposite())) {
                if (bl) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT) && !Utils.isReceivingRedstonePowerExcludingDirection(world, pos, state.get(FACING).getOpposite())) {
            world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }
}
