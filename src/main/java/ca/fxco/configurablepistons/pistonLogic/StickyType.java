package ca.fxco.configurablepistons.pistonLogic;

public enum StickyType {

    /*
     * All mods that make custom piston handlers/custom pushing logic need to take into account the StickyType!
     */

    /**
     * If this side of the block should not be able to be stuck too
     */
    NO_STICK,

    /**
     * Act's like Default although Block will not stick under certain conditions - W.I.P.
     */
    WEAK,

    /**
     * Normal Sticky Behavior, so connects to sticky blocks but itself is not sticky
     */
    DEFAULT,

    /**
     * Normal Sticky Behavior and its sticky (Like slime blocks)
     */
    STICKY,

    /**
     * Will be perfectly fused together, no separation
     */
    STRONG,

    /**
     * Strong except it bypasses the ConfigurablePistonBehavior checks, use `canBypassFused()` to prevent this - W.I.P.
     */
    FUSED
}
