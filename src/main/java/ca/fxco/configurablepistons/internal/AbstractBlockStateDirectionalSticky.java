package ca.fxco.configurablepistons.internal;

import ca.fxco.configurablepistons.helpers.StickyType;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.List;

public interface AbstractBlockStateDirectionalSticky {

    /*
     * This interface is for internal use only. Use ConfigurablePistonStickiness for single block conditions
     */

    // These methods are only used if `usesConfigurablePistonStickiness` return true
    // This allows for more configurable & conditional sticky block logic
    boolean usesConfigurablePistonStickiness();
    boolean isSticky();
    List<Pair<Direction, StickyType>> stickySides();
    StickyType sideStickiness(Direction direction);
}
