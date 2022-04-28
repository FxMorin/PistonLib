package ca.fxco.configurablepistons.helpers;

public enum StickyType {

    /**
     * If this side of the block should not be able to be stuck too on this side
     */
    NO_STICK,

    /**
     * Block will not stick under certain conditions - W.I.P.
     */
    WEAK,

    /**
     * Normal Sticky Behavior
     */
    DEFAULT,

    /**
     * Will be perfectly fused together, no separation - W.I.P.
     */
    STRONG,

    /**
     * Strong except it bypasses the ConfigurablePistonBehavior checks, use `canBypassFused()` to prevent this - W.I.P.
     */
    FUSED,

    /**
     * Sticks to all blocks no matter what type they are. E.x. Honey & Slime - W.I.P.
     */
    ALL,

    /**
     * Should be used for custom behavior in your mod. Will act like default for other mods
     * BEWARE other mods may be using this also, a better solution is on the todo list
     */
    CUSTOM
}
