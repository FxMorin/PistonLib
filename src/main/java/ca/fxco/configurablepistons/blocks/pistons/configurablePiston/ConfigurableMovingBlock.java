package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
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
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
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
    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                               Direction facing, boolean extending, boolean source) {
        return new ConfigurableMovingBlockEntity(extending ? extendingSpeed : retractingSpeed, translocation,
                pos, state, pushedBlock, facing, extending, source, this);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY);
    }

    /*@Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (canExtendOnRetracting && world.getBlockEntity(pos) instanceof PistonBlockEntity pistonBlockEntity) {
            if (pistonBlockEntity.source && pistonBlockEntity.pushedBlock.getBlock() instanceof BasicPistonBlock) {
                if (!pistonBlockEntity.isExtending()) {
                    System.out.println("Skipped Retracted on Extension!");
                    world.addSyncedBlockEvent(pos, this, 0, 0);
                }
            }
        }
    }

    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (type == 0) {
            if (world.getBlockEntity(pos) instanceof BasicPistonBlockEntity pbe && pbe.source &&
                    pbe.pushedBlock.getBlock() instanceof BasicPistonBlock basicPistonBlock) {
                //pbe.facing = pbe.facing.getOpposite();
                //pbe.progress = pbe.lastProgress = 1 - pbe.progress;
                float progress = pbe.progress;
                //pbe.extending = !pbe.extending;
                //pbe.progress = pbe.lastProgress = 1 - pbe.progress;
                BlockPos facingPos = pos.offset(pbe.facing);
                BlockState facingState = world.getBlockState(facingPos);
                if (facingState.isOf(this) &&
                        world.getBlockEntity(facingPos) instanceof BasicPistonBlockEntity bpbe &&
                        bpbe.facing == pbe.facing && bpbe.progress == progress) {
                    bpbe.extending = !bpbe.extending;
                    //bpbe.progress = bpbe.lastProgress = 1 - bpbe.progress;
                    changeBlockEntitiesTogether(world, facingPos, bpbe.pushedBlock, pbe.facing, progress);
                }
            }
        }
        return true;
    }

    private void changeBlockEntitiesTogether(World world, BlockPos pos, BlockState state,
                                             Direction movement, float progress) {
        ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness)state.getBlock();
        if (stick.usesConfigurablePistonStickiness() && stick.isSticky(state)) {
            //pistonBlockEntity.facing = pistonBlockEntity.facing.getOpposite();
            //pistonBlockEntity.progress = pistonBlockEntity.lastProgress = 1 - pistonBlockEntity.progress;
            for (Map.Entry<Direction,StickyType> sideData : stick.stickySides(state).entrySet()) {
                StickyType stickyType = sideData.getValue();
                if (stickyType.ordinal() < StickyType.STRONG.ordinal()) continue; // Only strong or fused
                Direction direction = sideData.getKey();
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = world.getBlockState(blockPos);
                if (blockState2.isOf(this) &&
                        world.getBlockEntity(blockPos) instanceof BasicPistonBlockEntity bpbe &&
                        bpbe.facing == movement && bpbe.progress == progress) {
                    bpbe.extending = !bpbe.extending;
                    //bpbe.progress = bpbe.lastProgress = 1 - bpbe.progress;
                    changeBlockEntitiesTogether(world, blockPos, bpbe.pushedBlock, movement, progress);
                }
            }
        }
    }*/

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
