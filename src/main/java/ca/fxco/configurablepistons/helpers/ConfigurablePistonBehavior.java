package ca.fxco.configurablepistons.helpers;

import net.minecraft.block.BlockState;

public interface ConfigurablePistonBehavior {

    /*
     * This only applies to blocks that are registered in the `configurable_piston_behavior` tagkey, all other
     * blocks will use the default minecraft checks if they are not in this group!
     * Only use this system if the default piston behavior is unable to achieve the results you want!
     */

    // These methods are only used if `usesConfigurablePistonBehavior` return true
    // This allows for more configurable & conditional piston behavior
    // I plan to pass more info through these methods later

    // This must return true in order for the configurable piston behavior to be used!
    default boolean usesConfigurablePistonBehavior(BlockState state) {
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

    // Only called on pushing, may add pulling later
    default boolean canDestroy(BlockState state) {
        return false;
    }
}
