package ca.fxco.pistonlib.gametest.expansion;

import ca.fxco.pistonlib.config.ParsedValue;

// Unused currently
public @interface Config {

    /**
     * Strings must be a valid config field names
     */
    String[] value();

    /**
     * Test Class that need to be met in order for the config option to be added/removed from the list of config options.
     * If the value is already within the list, when this test return false it will remove that option from the list.
     * However, if the value is not already in that list, when the test returns true, it will add it to the list.
     */
    Class<? extends ConfigOptionTest>[] testClass() default {};

    /**
     * The interface to test if the config option should be used or removed
     */
    interface ConfigOptionTest {
        /**
         * This test is run for all different combinations of config options used.
         * @param configValues An array of all the parsed config values currently enabled
         * @param value The field name of the config you are currently trying to add/remove
         * @param state Returns `true` if the value is currently in the list (to be removed), and false if it's not (to be added)
         */
        boolean shouldPass(ParsedValue<?>[] configValues, String value, boolean state);
    }
}
