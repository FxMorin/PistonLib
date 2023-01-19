package ca.fxco.pistonlib.blocks.halfBlocks;

import ca.fxco.pistonlib.impl.BlockPowerRedirection;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.redstone.Redstone;

public class HalfPoweredBlock extends Block implements ConfigurablePistonStickiness, BlockPowerRedirection {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HalfPoweredBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        Direction directionFrom = dir.getOpposite();
        Direction facing = state.getValue(FACING);
        if (directionFrom == facing) { // front
            return Redstone.SIGNAL_MAX;
        }
        if (facing != dir && facing != Direction.UP) { // side
            Block block = world.getBlockState(pos.relative(directionFrom)).getBlock();
            if (block instanceof DiodeBlock || block instanceof RedStoneWireBlock) {
                return facing.getAxis() != Direction.Axis.Y ? 8 : Redstone.SIGNAL_MAX;
            }
        }
        return Redstone.SIGNAL_NONE;
    }

    @Override
    public boolean canRedirectRedstone(BlockState state, Direction direction) {
        Direction facing = state.getValue(FACING);
        return direction != null && (direction.getOpposite() == facing) ||
                (facing != direction && facing != Direction.UP && direction.getAxis() != Direction.Axis.Y);
    }
}
