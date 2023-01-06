package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import static ca.fxco.configurablepistons.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonExtensionBlock extends BasicPistonExtensionBlock {

    protected final boolean slippery;
    protected final float extendingSpeed;
    protected final float retractingSpeed;
    protected final boolean translocation;
    protected final boolean verySticky;
    protected final boolean canExtendOnRetracting;

    public ConfigurablePistonExtensionBlock(ConfigurablePistonBlock.Settings pistonSettings) {
        this(BasicPistonExtensionBlock.getDefaultSettings(), pistonSettings);
    }

    public ConfigurablePistonExtensionBlock(AbstractBlock.Settings settings,
                                            ConfigurablePistonBlock.Settings pistonSettings) {
        super(settings);
        slippery = pistonSettings.slippery;
        extendingSpeed = pistonSettings.extendingSpeed;
        retractingSpeed = pistonSettings.retractingSpeed;
        translocation = pistonSettings.translocation;
        verySticky = pistonSettings.verySticky;
        canExtendOnRetracting = pistonSettings.canExtendOnRetracting;
    }

    @Override
    public BlockEntity createPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                               Direction facing, boolean extending, boolean source) {
        return new ConfigurablePistonBlockEntity(extending ? extendingSpeed : retractingSpeed, translocation,
                pos, state, pushedBlock, facing, extending, source, this);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World w, BlockState state, BlockEntityType<T> t) {
        return checkType(t, ModBlockEntities.CONFIGURABLE_PISTON_BLOCK_ENTITY, ConfigurablePistonBlockEntity::tick);
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
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (slippery && !oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null) {
            world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (slippery && !world.isClient()) {
            world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
        return state;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (slippery) {
            int i = BaseSlipperyBlock.calculateDistance(world, pos);
            BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
            if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
                FallingBlockEntity.spawnFromBlock(world, pos, blockState);
            } else if (state != blockState) {
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
            }
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !slippery || BaseSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
        if (slippery) {
            builder.add(SLIPPERY_DISTANCE);
        }
    }
}
