package ca.fxco.pistonlib.blocks.halfBlocks;

import ca.fxco.pistonlib.helpers.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class HalfRedstoneLampBlock extends RedstoneLampBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public HalfRedstoneLampBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean isLit = state.getValue(LIT);
            boolean shouldBeLit = Utils.hasNeighborSignalExceptFromFacing(level, pos, state.getValue(FACING).getOpposite());

            if (isLit != shouldBeLit) {
                if (shouldBeLit) {
                    level.setBlock(pos, state.cycle(LIT), UPDATE_CLIENTS);
                } else {
                    level.scheduleTick(pos, this, 4);
                }
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && !Utils.hasNeighborSignalExceptFromFacing(level, pos, state.getValue(FACING).getOpposite())) {
            level.setBlock(pos, state.cycle(LIT), UPDATE_CLIENTS);
        }
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
        builder.add(FACING, LIT);
    }
}
