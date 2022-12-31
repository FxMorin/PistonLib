package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public class PoweredStickyBlock extends FacingBlock implements ConfigurablePistonStickiness {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public PoweredStickyBlock(Settings settings) {
        super(settings);
    }

    public void updateState(BlockState state, World world, BlockPos pos, boolean force) {
        boolean isPowered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean powered = state.get(POWERED);
        if (isPowered && !powered) {
            world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_LISTENERS);
        } else if (!isPowered && powered) {
            world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            updateState(state, world, pos, false);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient() && !state.isOf(newState.getBlock()) && moved && state.get(POWERED)) {
            updateState(state, world, pos, true);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock()) && state.get(POWERED)) {
            updateState(state, world, pos, true);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public boolean isSticky(BlockState state) {
        return state.get(POWERED);
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.get(FACING), StickyType.STICKY); // Sticky Front
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        return direction == state.get(FACING) ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
