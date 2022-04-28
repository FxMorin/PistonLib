package ca.fxco.configurablepistons.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface ConfigurablePistonStickiness {

    /*
     * These methods are only used if `usesConfigurablePistonStickiness` returns true
     * This allows for more configurable & conditional piston stickiness
     */

    // This must return true in order for the configurable piston stickiness to be used!
    default boolean usesConfigurablePistonStickiness() {
        return false;
    }

    // If the block is currently sticky for any side, for quick checks to boost performance by skipping more intensive checks early
    // For some checks it might just be faster to set this to true!
    default boolean isSticky(BlockState state) {
        return true;
    }

    // Returns a list of directions that are sticky, and the stickyType.
    default List<Pair<Direction, StickyType>> stickySides(BlockState state) {
        return List.of();
    }

    default StickyType sideStickiness(BlockState state, Direction direction) {
        return StickyType.DEFAULT;
    }
}
