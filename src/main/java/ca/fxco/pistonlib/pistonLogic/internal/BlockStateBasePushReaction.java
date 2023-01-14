package ca.fxco.pistonlib.pistonLogic.internal;

import net.minecraft.core.Direction;

public interface BlockStateBasePushReaction {

    int getWeight();

    /*
     * This interface is for internal use only. Use ConfigurablePistonBehavior for single block conditions
     */

    // These methods are only used if `usesConfigurablePistonBehavior` return true
    // This allows for more configurable & conditional piston behavior
    boolean usesConfigurablePistonBehavior();
    boolean isMovable();
    boolean canPistonPush(Direction direction);
    boolean canPistonPull(Direction direction);
    boolean canBypassFused();
    boolean canDestroy();
}
