package ca.fxco.configurablepistons.blocks.pistons.configurablePiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Map;

import static ca.fxco.configurablepistons.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock.EXTENDED;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.configurablepistons.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonHeadBlock extends BasicPistonHeadBlock
        implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    private final boolean slippery;
    private final boolean verySticky;

    public ConfigurablePistonHeadBlock(ConfigurablePistonBlock.Settings pistonSettings) {
        this(FabricBlockSettings.copyOf(Blocks.PISTON_HEAD), pistonSettings);
    }

    public ConfigurablePistonHeadBlock(Settings settings, ConfigurablePistonBlock.Settings pistonSettings) {
        super(settings);
        slippery = pistonSettings.slippery;
        verySticky = pistonSettings.verySticky;
        if (slippery) {
            this.setDefaultState(this.getDefaultState().with(SLIPPERY_DISTANCE, 0));
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (slippery && !oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null) {
            world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (slippery && !world.isClient()) {
            world.scheduleBlockTick(pos, this, SLIPPERY_DELAY);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (slippery) {
            int i = BaseSlipperyBlock.calculateDistance(world, pos);
            BlockState blockState = state.with(SLIPPERY_DISTANCE, i);
            if (blockState.get(SLIPPERY_DISTANCE) == MAX_DISTANCE && !super.canPlaceAt(state, world, pos)) {
                BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
                if (this.isAttached(state, world.getBlockState(blockPos))) {
                    FallingBlockEntity.spawnFromBlock(world, blockPos, world.getBlockState(blockPos).with(EXTENDED, false));
                }
                world.removeBlock(pos, false);
            } else if (state != blockState) {
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (!slippery || BaseSlipperyBlock.calculateDistance(world, pos) < MAX_DISTANCE) {
            if (verySticky) {
                BlockState blockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
                return this.isAttached(state, blockState) || blockState.isIn(ModTags.MOVING_PISTONS);
            }
            return super.canPlaceAt(state, world, pos);
        }
        if (verySticky) {
            BlockState blockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
            return this.isAttached(state, blockState) || blockState.isIn(ModTags.MOVING_PISTONS);
        }
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT);
        if (slippery) {
            builder.add(SLIPPERY_DISTANCE);
        }
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return verySticky; // Makes the piston head movable by bypassing vanilla checks
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return verySticky;
    }

    // Returns a list of directions that are sticky, and the stickyType.
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.get(FACING), StickyType.STICKY, state.get(FACING).getOpposite(), StickyType.STICKY);
    }

    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.get(FACING).getAxis() == direction.getAxis() ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
