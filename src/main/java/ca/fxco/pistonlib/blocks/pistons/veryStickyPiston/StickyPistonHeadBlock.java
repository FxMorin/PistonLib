package ca.fxco.pistonlib.blocks.pistons.veryStickyPiston;

import java.util.Map;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class StickyPistonHeadBlock extends BasicPistonHeadBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public StickyPistonHeadBlock() {
        super();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        return this.isFittingBase(state, blockState) || blockState.is(ModTags.MOVING_PISTONS);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.getValue(FACING), StickyType.STICKY, state.getValue(FACING).getOpposite(), StickyType.STICKY);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.getValue(FACING).getAxis() == direction.getAxis() ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
