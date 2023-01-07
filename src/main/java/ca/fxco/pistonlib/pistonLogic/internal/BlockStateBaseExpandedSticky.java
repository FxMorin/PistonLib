package ca.fxco.pistonlib.pistonLogic.internal;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.StickyType;

import net.minecraft.core.Direction;

public interface BlockStateBaseExpandedSticky {

    /*
     * This interface is for internal use only. Use ConfigurablePistonStickiness for single block conditions
     */

    // Defines the stickyGroup that this block uses
    @Nullable StickyGroup getStickyGroup();

    default boolean hasStickyGroup() {
        return getStickyGroup() != null;
    }

    // These methods are only used if `usesConfigurablePistonStickiness` return true
    // This allows for more configurable & conditional sticky block logic
    boolean usesConfigurablePistonStickiness();
    boolean isSticky();
    Map<Direction, StickyType> stickySides();
    StickyType sideStickiness(Direction direction);

}
