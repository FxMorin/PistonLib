package ca.fxco.pistonlib.blocks.pistons.slipperyPiston;

import ca.fxco.pistonlib.base.ModProperties;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock.EXTENDED;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class SlipperyPistonHeadBlock extends BasicPistonHeadBlock {

    public static final IntegerProperty SLIPPERY_DISTANCE = ModProperties.SLIPPERY_DISTANCE;

    public SlipperyPistonHeadBlock(PistonFamily family) {
        super(family);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(SHORT, false)
            .setValue(SLIPPERY_DISTANCE, 0));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && !world.isClientSide() && world.getBlockEntity(pos) == null)
            world.scheduleTick(pos, this, SLIPPERY_DELAY);
        super.onPlace(state, world, pos, oldState, movedByPiston);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = BaseSlipperyBlock.calculateDistance(level, pos);
        BlockState blockState = state.setValue(SLIPPERY_DISTANCE, i);
        if (blockState.getValue(SLIPPERY_DISTANCE) >= MAX_DISTANCE && !super.canSurvive(state, level, pos)) {
            BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
            if (this.isFittingBase(state, level.getBlockState(blockPos)))
                FallingBlockEntity.fall(level, blockPos, level.getBlockState(blockPos).setValue(EXTENDED,false));
            level.removeBlock(pos,false);
        } else if (state != blockState) {
            level.setBlock(pos, blockState, UPDATE_ALL);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE || super.canSurvive(state, level, pos);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT, SLIPPERY_DISTANCE);
    }
}
