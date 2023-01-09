package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

import static ca.fxco.configurablepistons.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurableMovingBlock extends BasicMovingBlock {

    protected final boolean slippery;
    protected final float extendingSpeed;
    protected final float retractingSpeed;
    protected final boolean translocation;
    protected final boolean verySticky;
    protected final boolean canExtendOnRetracting;

    public ConfigurableMovingBlock(ConfigurablePistonBaseBlock.Settings pistonSettings) {
        this(BasicMovingBlock.createDefaultSettings(), pistonSettings);
    }

    public ConfigurableMovingBlock(BlockBehaviour.Properties properties,
                                   ConfigurablePistonBaseBlock.Settings pistonSettings) {
        super(properties);
        slippery = pistonSettings.slippery;
        extendingSpeed = pistonSettings.extendingSpeed;
        retractingSpeed = pistonSettings.retractingSpeed;
        translocation = pistonSettings.translocation;
        verySticky = pistonSettings.verySticky;
        canExtendOnRetracting = pistonSettings.canExtendOnRetracting;
    }

    @Override
    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               @Nullable BlockEntity movedBlockEntity, Direction facing,
                                               boolean extending, boolean isSourcePiston) {
        return new ConfigurableMovingBlockEntity(extending ? extendingSpeed : retractingSpeed, translocation,
                pos, state, movedState, facing, extending, isSourcePiston, this);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (canExtendOnRetracting && level.getBlockEntity(pos) instanceof BasicMovingBlockEntity movingBlockEntity) { //TODO: Merge into single statement
            if (movingBlockEntity.isSourcePiston && movingBlockEntity.movedState.getBlock() instanceof ConfigurablePistonBaseBlock cpbb) {
                if (!movingBlockEntity.isExtending()) {
                    Direction facing = movingBlockEntity.movedState.getValue(FACING);
                    if (cpbb.hasNeighborSignal(level, pos, facing)) {
                        float progress = movingBlockEntity.progress;
                        movingBlockEntity.finalTick();
                        boolean temp = false;
                        BlockPos frontPos = pos.relative(facing);
                        if (level.getBlockEntity(frontPos) instanceof BasicMovingBlockEntity bmbe && !bmbe.isSourcePiston && !bmbe.extending && bmbe.progress == progress) {
                            bmbe.finalTick();
                            temp = true;
                        }
                        cpbb.checkIfExtend(level, pos, movingBlockEntity.movedState);
                        int progressInt = Float.floatToIntBits(1 - progress);
                        level.blockEvent(frontPos, this, 99, progressInt);
                        if (temp) {
                            level.blockEvent(frontPos.relative(facing), this, 99, progressInt);
                        }
                    }
                }
            }
        }
    }

    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int type, int data) {
        if (type == 99 && level.getBlockEntity(blockPos) instanceof BasicMovingBlockEntity bmbe) {
            bmbe.progress = bmbe.progressO = Float.intBitsToFloat(data);
        }
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (slippery && !oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) == null) {
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level,
                                  BlockPos pos, BlockPos neighborPos) {
        if (slippery && !level.isClientSide()) {
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (slippery) {
            int i = BaseSlipperyBlock.calculateDistance(level, pos);
            BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
            if (blockState.getValue(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
                FallingBlockEntity.fall(level, pos, blockState);
            } else if (state != blockState) {
                level.setBlock(pos, blockState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !slippery || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
        if (slippery) {
            builder.add(SLIPPERY_DISTANCE);
        }
    }
}
