package ca.fxco.configurablepistons.newBlocks.veryStickyPiston;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.helpers.StickyType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class VeryStickyPistonBlock extends BasicPistonBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public VeryStickyPistonBlock() {
        super(true);
    }

    public VeryStickyPistonBlock(StickyPistonExtensionBlock extensionBlock, BasicPistonHeadBlock headBlock) {
        super(true, extensionBlock, headBlock);
    }

    // Automatically makes it movable even when extended
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
        return state.get(FACING) == direction ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
