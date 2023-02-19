package ca.fxco.pistonlib.blocks.pistons.slipperyPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.MAX_DISTANCE;
import static ca.fxco.pistonlib.blocks.slipperyBlocks.BaseSlipperyBlock.SLIPPERY_DELAY;

public class SlipperyPistonBaseBlock extends BasicPistonBaseBlock {

    public SlipperyPistonBaseBlock(PistonFamily family, PistonType type) {
        super(family, type);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide() && level.getBlockEntity(pos) == null) {
            this.checkIfExtend(level, pos, state);
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide())
            level.scheduleTick(pos, this, SLIPPERY_DELAY);
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (BaseSlipperyBlock.calculateDistance(level, pos) >= MAX_DISTANCE)
            FallingBlockEntity.fall(level, pos, state.setValue(EXTENDED,false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return BaseSlipperyBlock.calculateDistance(level, pos) < MAX_DISTANCE;
    }
}
