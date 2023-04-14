package ca.fxco.pistonlib.helpers;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Like AtomicReference except it's not volatile and not Atomic it's mostly used as a holder for lambda's
 */
public class IonicReference<I> {

    private I value;

    /**
     * Creates a new IonicReference with the given initial value.
     *
     * @param initialValue the initial value
     */
    public IonicReference(I initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new IonicReference with null initial value.
     */
    public IonicReference() {
    }

    public final void set(I value) {
        this.value = value;
    }

    public final I get() {
        return this.value;
    }

    /**
     * Sets the value to {@code newValue} and returns the old value,
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final I getAndSet(I newValue) {
        I oldValue = get();
        this.value = newValue;
        return oldValue;
    }

    /**
     * Updates the current value with the results of applying the given function
     *
     * @param updateFunction a function
     * @return the previous value
     */
    public final I getAndUpdate(UnaryOperator<I> updateFunction) {
        I oldValue = get();
        this.value = updateFunction.apply(oldValue);
        return oldValue;
    }

    /**
     * Updates the current value with the results of applying the given function
     * to the current and given values, returning the previous value.
     * The function is applied with the current value as its first argument,
     * and the given update as the second argument.
     *
     * @param x the update value
     * @param accumulatorFunction a function of two arguments
     * @return the previous value
     */
    public final I getAndAccumulate(I x, BinaryOperator<I> accumulatorFunction) {
        I oldValue = get();
        this.value = accumulatorFunction.apply(oldValue, x);
        return oldValue;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return String.valueOf(get());
    }
}
