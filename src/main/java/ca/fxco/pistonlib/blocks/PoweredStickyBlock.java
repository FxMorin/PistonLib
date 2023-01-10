package ca.fxco.pistonlib.blocks;

import java.util.Map;

import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PoweredStickyBlock extends DirectionalBlock implements ConfigurablePistonStickiness {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public PoweredStickyBlock(Properties properties) {
        super(properties);
    }

    public void updatePowered(BlockState state, Level level, BlockPos pos, boolean force) {
    	boolean isPowered = state.getValue(POWERED);
        boolean shouldBePowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());

        if (isPowered != shouldBePowered) {
            level.setBlock(pos, state.setValue(POWERED, shouldBePowered), UPDATE_ALL);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            updatePowered(state, level, pos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide() && !newState.is(this) && movedByPiston && state.getValue(POWERED)) {
            updatePowered(state, level, pos, true);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && !oldState.is(this) && state.getValue(POWERED)) {
            updatePowered(state, level, pos, true);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection());
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public boolean isSticky(BlockState state) {
        return state.getValue(POWERED);
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.getValue(FACING), StickyType.STICKY); // Sticky Front
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir == state.getValue(FACING) ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
