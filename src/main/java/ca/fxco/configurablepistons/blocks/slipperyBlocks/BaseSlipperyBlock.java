package ca.fxco.configurablepistons.blocks.slipperyBlocks;

import ca.fxco.configurablepistons.base.ModProperties;
import ca.fxco.configurablepistons.base.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BaseSlipperyBlock extends Block {

    public static final int SLIPPERY_DELAY = 6;
    public static final int MAX_DISTANCE = 12;

    public static final IntegerProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    /**
     * A block so slippery that it just keeps falling apart unless it's connected to other blocks
     */

    public BaseSlipperyBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.stateDefinition.any().setValue(SLIPPERY_DISTANCE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(SLIPPERY_DISTANCE, calculateDistance(ctx.getLevel(), ctx.getClickedPos()));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level,
                                  BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = calculateDistance(level, pos);
        BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
        if (blockState.getValue(SLIPPERY_DISTANCE) == MAX_DISTANCE) {
            FallingBlockEntity.fall(level, pos, blockState);
        } else if (state != blockState) {
            level.setBlock(pos, blockState, UPDATE_ALL);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return calculateDistance(level, pos) < MAX_DISTANCE;
    }

    public static int calculateDistance(BlockGetter level, BlockPos pos) {
        return calculateDistance(level, pos, 0);
    }

    private static int calculateDistance(BlockGetter level, BlockPos pos, int depth) {
        MutableBlockPos mutable = pos.mutable().move(Direction.DOWN);
        BlockState blockState = level.getBlockState(mutable);
        int currentDistance = MAX_DISTANCE;
        if (blockState.is(ModTags.SLIPPERY_BLOCKS)) {
            currentDistance = blockState.getValue(SLIPPERY_DISTANCE);
        } else if (blockState.is(ModTags.SLIPPERY_TRANSPARENT_BLOCKS)) {
            return depth < MAX_DISTANCE ? calculateDistance(level, mutable, ++depth) : MAX_DISTANCE;
        } else if (!blockState.is(ModTags.SLIPPERY_IGNORE_BLOCKS) &&
                blockState.isFaceSturdy(level, mutable, Direction.UP)) {
            return 0;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState2 = level.getBlockState(mutable.setWithOffset(pos, direction));
            if (blockState2.is(ModTags.SLIPPERY_BLOCKS)) {
                currentDistance = Math.min(currentDistance, blockState2.getValue(SLIPPERY_DISTANCE) + 1);
                if (currentDistance == 1) break;
            }
        }
        return currentDistance;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLIPPERY_DISTANCE);
    }
}
