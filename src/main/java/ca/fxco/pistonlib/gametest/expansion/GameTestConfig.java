package ca.fxco.pistonlib.gametest.expansion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GameTestConfig {

    /**
     * All strings must be a valid config field name. These fields will be cycled through to make sure they also pass
     */
    String[] value() default {};

    /**
     * If this test should run when these config values are present, and with what settings
     */
    RunState runState() default @RunState();

    /**
     * For more control over how config options pass & fail. You can use @Config to specify the new result
     */
    //Config[] configs() default {}; // TODO
}
