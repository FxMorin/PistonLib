package ca.fxco.pistonlib.blocks.pistons.slipperyPiston;

import ca.fxco.pistonlib.base.ModProperties;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class SlipperyMovingBlock extends BasicMovingBlock {

    public static final IntegerProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    public SlipperyMovingBlock(PistonFamily family) {
        super(family);

        this.registerDefaultState(this.stateDefinition.any().setValue(SLIPPERY_DISTANCE, 0));
    }

    public SlipperyMovingBlock(PistonFamily family, Properties properties) {
        super(family, properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(this) && !level.isClientSide() && level.getBlockEntity(pos) == null)
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int distance = state.getValue(SLIPPERY_DISTANCE);
        int nextDistance = BaseSlipperyBlock.calculateDistance(world, pos);

        if (nextDistance >= MAX_DISTANCE) {
            FallingBlockEntity.fall(world, pos, state);
        } else if (nextDistance != distance) {
            world.setBlock(pos, state.setValue(SLIPPERY_DISTANCE, nextDistance), UPDATE_ALL);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SLIPPERY_DISTANCE);
    }
}
