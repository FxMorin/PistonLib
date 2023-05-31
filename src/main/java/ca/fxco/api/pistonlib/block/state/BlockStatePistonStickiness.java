package ca.fxco.api.pistonlib.block.state;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStatePistonStickiness {

    /*
     * This interface is for internal use only. Use ConfigurablePistonStickiness for single block conditions
     */

    // Defines the stickyGroup that this block uses
    @Nullable StickyGroup pl$getStickyGroup();

    boolean pl$hasStickyGroup();

    // These methods are only used if `usesConfigurablePistonStickiness` return true
    // This allows for more configurable & conditional sticky block logic
    boolean pl$usesConfigurablePistonStickiness();

    boolean pl$isSticky();

    Map<Direction, StickyType> pl$stickySides();

    StickyType pl$sideStickiness(Direction dir);

    /**
     * This only gets used if the sticky type is {@linkplain ca.fxco.pistonlib.pistonLogic.StickyType#CONDITIONAL CONDITIONAL}.
     */
    boolean pl$matchesStickyConditions(BlockState neighborState, Direction dir);

}
