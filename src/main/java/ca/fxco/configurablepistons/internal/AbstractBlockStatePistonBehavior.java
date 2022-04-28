package ca.fxco.configurablepistons.internal;

public interface AbstractBlockStatePistonBehavior {

    /*
     * This interface is for internal use only. Use ConfigurablePistonBehavior for single block conditions
     */

    // These methods are only used if `usesConfigurablePistonBehavior` return true
    // This allows for more configurable & conditional piston behavior
    boolean usesConfigurablePistonBehavior();
    boolean isMovable();
    boolean canPistonPush();
    boolean canPistonPull();
    boolean canBypassFused();
    boolean canDestroy();
}
