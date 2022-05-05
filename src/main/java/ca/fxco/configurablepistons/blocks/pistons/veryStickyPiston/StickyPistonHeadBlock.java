package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.Map;

public class StickyPistonHeadBlock extends BasicPistonHeadBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public StickyPistonHeadBlock() {
        super();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return this.isAttached(state, blockState) || blockState.isIn(ModTags.MOVING_PISTONS);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    // Returns a list of directions that are sticky, and the stickyType.
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.get(FACING), StickyType.STICKY, state.get(FACING).getOpposite(), StickyType.STICKY);
    }

    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.get(FACING).getAxis() == direction.getAxis() ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
