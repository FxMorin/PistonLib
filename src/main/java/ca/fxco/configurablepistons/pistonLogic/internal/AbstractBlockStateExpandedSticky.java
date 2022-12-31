package ca.fxco.configurablepistons.pistonLogic.internal;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface AbstractBlockStateExpandedSticky {

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
