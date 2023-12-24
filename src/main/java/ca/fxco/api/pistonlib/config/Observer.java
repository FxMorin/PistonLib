package ca.fxco.api.pistonlib.config;

import ca.fxco.pistonlib.config.ParsedValue;

/**
 * The Observer class is used within the {@link ConfigValue} class in order to trigger events
 */
public interface Observer<T> {

    /**
     * Called when the config option is first loaded, or when the config manager is reset,
     * and it reloads it from the config
     * @param parsedValue the current parsed value, already set to this value
     * @param isDefault If the config option is the default value
     */
    void onLoad(ParsedValue<T> parsedValue, boolean isDefault);

    /**
     * Called whenever the config option changes value.
     * You can use `parsedValue.setValue(value)` to change the value within the method call, however it's not recommended.
     * Changing the value should be done within `Parser` whenever possible!
     * @param parsedValue The parsed value, it will already have the new value
     * @param oldValue the value that was originally set
     * @param newValue the value that is now currently being used
     */
    void onChange(ParsedValue<T> parsedValue, T oldValue, T newValue);

}
