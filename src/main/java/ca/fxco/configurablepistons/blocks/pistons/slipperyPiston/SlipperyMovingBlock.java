package ca.fxco.configurablepistons.blocks.pistons.slipperyPiston;

import org.jetbrains.annotations.Nullable;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModProperties;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class SlipperyMovingBlock extends BasicMovingBlock {

    public static final IntegerProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    public SlipperyMovingBlock() {
        super();

        this.registerDefaultState(this.stateDefinition.any().setValue(SLIPPERY_DISTANCE, 0));
    }

    @Override
    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               @Nullable BlockEntity movedBlockEntity, Direction facing,
                                               boolean extending, boolean isSourcePiston) {
        return new SlipperyMovingBlockEntity(pos, state, movedState, facing, extending, isSourcePiston);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY);
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
