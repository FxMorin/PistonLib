package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class StickyPistonHeadBlock extends BasicPistonHeadBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public StickyPistonHeadBlock() {
        super();
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
        return Map.of(state.get(FACING),StickyType.STICKY); // Sticky Front
    }

    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.get(FACING) == direction.getOpposite() ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
