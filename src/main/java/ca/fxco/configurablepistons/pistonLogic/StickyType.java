package ca.fxco.configurablepistons.pistonLogic;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public enum StickyType {

    /*
     * All mods that make custom piston handlers/custom pushing logic need to take into account the StickyType!
     */

    /**
     * If this side of the block should not be able to be stuck too
     */
    NO_STICK,

    /**
     * Sticky if another sticky block is next to it, and it matches the conditions
     */
    CONDITIONAL,

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
    FUSED;


    /**
     * Should only be called if CONDITIONAL
     */
    public boolean canStick(BlockState state, BlockState adjState, Direction dir) {
        return ((ConfigurablePistonStickiness)state.getBlock()).matchesStickyConditions(state, adjState, dir);
    }
}
