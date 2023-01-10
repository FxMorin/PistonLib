package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ca.fxco.pistonlib.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

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
                pos, state, movedState, facing, extending, isSourcePiston);
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
                        movingBlockEntity.finalTick(false, false);
                        Set<BlockPos> positions = new HashSet<>();
                        BlockPos frontPos = pos.relative(facing);
                        if (level.getBlockEntity(frontPos) instanceof ConfigurableMovingBlockEntity bmbe && !bmbe.extending && bmbe.progress == progress) {
                            ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)bmbe.movedState.getBlock();
                            if (stick.usesConfigurablePistonStickiness() && stick.isSticky(bmbe.movedState)) {
                                stuckNeighbors(level, frontPos, stick.stickySides(bmbe.movedState), bmbe, positions);
                            }
                            bmbe.finalTick();
                        }
                        //stuckNeighbors(level, pos.relative(facing), );
                        cpbb.checkIfExtend(level, pos, movingBlockEntity.movedState);
                        int progressInt = Float.floatToIntBits(1 - progress);
                        level.blockEvent(frontPos, this, 99, progressInt);
                        for (BlockPos pos9 : positions) {
                            level.blockEvent(pos9.relative(facing), this, 99, progressInt);
                        }
                    }
                }
            }
        }
    }

    private void stuckNeighbors(Level level, BlockPos pos, Map<Direction, StickyType> stickyTypes,
                                ConfigurableMovingBlockEntity thisMbe, Set<BlockPos> set) {
        for (Map.Entry<Direction, StickyType> entry : stickyTypes.entrySet()) {
            StickyType stickyType = entry.getValue();

            if (stickyType.ordinal() < StickyType.STRONG.ordinal()) { // only strong or fused
                continue;
            }

            Direction dir = entry.getKey();
            BlockPos neighborPos = pos.relative(dir);
            if (set.contains(neighborPos)) {
                continue;
            }
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.is(thisMbe.getMovingBlock())) {
                BlockEntity blockEntity = level.getBlockEntity(neighborPos);

                if (blockEntity instanceof ConfigurableMovingBlockEntity mbe) {
                    if (!mbe.isExtending() && thisMbe.progress == mbe.progress) {
                        set.add(neighborPos);
                        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)mbe.movedState.getBlock();
                        if (stick.usesConfigurablePistonStickiness() && stick.isSticky(mbe.movedState)) {
                            stuckNeighbors(level, neighborPos, stick.stickySides(mbe.movedState), mbe, set);
                        }
                        mbe.finalTick(true, true);
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
