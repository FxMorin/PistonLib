package ca.fxco.api.pistonlib.toggle;

import java.util.function.BooleanSupplier;

public interface ToggleableProperties<T> {

    /** Allows you to conditionally set if this object is disabled */
    T setDisabled(BooleanSupplier isDisabled);
}
