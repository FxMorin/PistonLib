package ca.fxco.api.pistonlib.config;

import ca.fxco.pistonlib.config.ParsedValue;
import org.jetbrains.annotations.Nullable;

/**
 * Use this interface to easily add ways to load/save custom types for the toml config file
 * This can be used to change what class toml uses to save your type
 */
public interface TypeConverter {

    /**
     * Attempts to save a value
     * @return null if it's unable to save this value
     */
    <T> @Nullable Object saveValue(T value, ParsedValue<T> parsedValue);

    /**
     * Attempts to load an Object
     * @return null if it's unable to load this value
     */
    <T> @Nullable T loadValue(Object value, ParsedValue<T> parsedValue);
}
