package ca.fxco.configurablepistons.helpers;

import net.minecraft.block.BlockState;

public interface ConfigurablePistonBehavior {

    /*
     * These methods are only used if `usesConfigurablePistonBehavior` returns true
     * This allows for more configurable & conditional piston behavior
     */

    // This must return true in order for the configurable piston behavior to be used!
    default boolean usesConfigurablePistonBehavior() {
        return false;
    }

    // If the block is currently movable, for quick checks to boost performance by skipping more intensive checks early
    default boolean isMovable(BlockState state) {
        return true;
    }

    default boolean canPistonPush(BlockState state) {
        return true;
    }

    default boolean canPistonPull(BlockState state) {
        return true;
    }

    default boolean canBypassFused(BlockState state) {
        return false;
    }

    // Only called on pushing, may add pulling later
    default boolean canDestroy(BlockState state) {
        return false;
    }
}
