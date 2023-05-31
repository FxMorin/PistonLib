package ca.fxco.api.pistonlib.item;

import java.util.function.BooleanSupplier;

import ca.fxco.api.pistonlib.toggle.Toggleable;

/**
 * This interface collects all custom behavior that is injected into {@code Item}s.
 * It provides dummy implementations for all custom behavior. The actual implementations
 * of these methods are provided in their respective Mixin classes.
 */
public interface PLItem extends Toggleable {


    // Toggleable

    @Override
    default BooleanSupplier pl$getIsDisabled() {
        return null;
    }
}
