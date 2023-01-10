package ca.fxco.pistonlib.blocks.pistons.configurablePiston;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.Map;

import static ca.fxco.pistonlib.base.ModProperties.SLIPPERY_DISTANCE;
import static ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock.EXTENDED;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class ConfigurablePistonHeadBlock extends BasicPistonHeadBlock
        implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    private final boolean slippery;
    private final boolean verySticky;

    public ConfigurablePistonHeadBlock(ConfigurablePistonBaseBlock.Settings pistonSettings) {
        this(FabricBlockSettings.copyOf(Blocks.PISTON_HEAD), pistonSettings);
    }

    public ConfigurablePistonHeadBlock(Properties properties, ConfigurablePistonBaseBlock.Settings settings) {
        super(properties);
        slippery = settings.slippery;
        verySticky = settings.verySticky;
        if (slippery) {
            this.registerDefaultState(this.defaultBlockState().setValue(SLIPPERY_DISTANCE, 0));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (slippery && !oldState.is(state.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) == null) {
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (slippery && !level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (slippery) {
            int i = BaseSlipperyBlock.calculateDistance(level, pos);
            BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
            if (blockState.getValue(SLIPPERY_DISTANCE) == MAX_DISTANCE && !super.canSurvive(state, level, pos)) {
                BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
                if (this.isFittingBase(state, level.getBlockState(blockPos))) {
                    FallingBlockEntity.fall(level, pos, state.setValue(EXTENDED,false));
                }
                level.removeBlock(pos, false);
            } else if (state != blockState) {
                level.setBlock(pos, blockState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!slippery || BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE) {
            if (verySticky) {
                BlockState blockState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
                return this.isFittingBase(state, blockState) || blockState.is(ModTags.MOVING_PISTONS);
            }
            return super.canSurvive(state, level, pos);
        }
        if (verySticky) {
            BlockState blockState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
            return this.isFittingBase(state, blockState) || blockState.is(ModTags.MOVING_PISTONS);
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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
        return Map.of(state.getValue(FACING), StickyType.STICKY,
                state.getValue(FACING).getOpposite(), StickyType.STICKY);
    }

    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.getValue(FACING).getAxis() == direction.getAxis() ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
