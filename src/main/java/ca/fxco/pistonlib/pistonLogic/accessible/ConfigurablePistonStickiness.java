package ca.fxco.pistonlib.pistonLogic.accessible;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface ConfigurablePistonStickiness {

    // Defines if this block can stick to the adjacent block. Only use this on sticky blocks
    default @Nullable StickyGroup getStickyGroup() {
        return null;
    }

    default boolean hasStickyGroup() {
        return getStickyGroup() != null;
    }

    /*
     * These methods are only used if `usesConfigurablePistonStickiness` returns true
     * This allows for more configurable & conditional piston stickiness
     */

    // This must return true in order for the configurable piston stickiness to be used!
    default boolean usesConfigurablePistonStickiness() {
        return false;
    }

    // If the block is currently sticky for any side, for quick checks to boost performance by
    // skipping more intensive checks early. For some checks it might just be faster to set this to true!
    default boolean isSticky(BlockState state) {
        return true;
    }

    // Returns a list of directions that are sticky, and the stickyType.
    default Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of();
    }

    default StickyType sideStickiness(BlockState state, Direction dir) {
        return StickyType.DEFAULT;
    }

    /**
     * This only gets used if the sticky type is {@linkplain ca.fxco.pistonlib.pistonLogic.StickyType#CONDITIONAL CONDITIONAL}.
     */
    default boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return true;
    }
}
