package ca.fxco.pistonlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Redstone;

public class AllSidedObserverBlock extends Block {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AllSidedObserverBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, state = state.cycle(POWERED), UPDATE_CLIENTS);
        if (state.getValue(POWERED)) {
            level.scheduleTick(pos, this, 2);
        }
        this.updateNeighbors(level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.getValue(POWERED) && !neighborState.isSignalSource()) {
            this.startSignal(level, pos);
        }

        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    private void startSignal(LevelAccessor level, BlockPos pos) {
        if (!level.isClientSide() && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 2);
        }
    }

    protected void updateNeighbors(Level world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos side = pos.relative(dir);

            world.neighborChanged(side, this, pos);
            world.updateNeighborsAtExceptFromFacing(side, this, dir);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getSignal(level, pos, dir);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(POWERED) ? Redstone.SIGNAL_MAX : Redstone.SIGNAL_NONE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && !oldState.is(this) && state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.setBlock(pos, state.setValue(POWERED, false), UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE);
            this.updateNeighbors(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide() && !newState.is(this) && state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            this.updateNeighbors(level, pos);
        }
    }
}
