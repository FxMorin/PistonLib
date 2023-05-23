package ca.fxco.pistonlib.gametest.expansion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTestConfig {

    /**
     * If the custom gametest blocks should be used for this test
     */
    boolean customBlocks() default true;

    /**
     * All strings must be a valid config field name. These fields will be cycled through to make sure they also pass
     */
    String[] value() default {};

    /**
     * For more control over how config options pass & fail. You can use @Config to specify the new result
     */
    Config[] config() default {};

    /**
     * If config options should be ignored
     */
    boolean ignored() default false;

    /**
     * Only run if all the config options are present.
     * If ignored, none of the config options must be present
     */
    boolean combined() default false;
}
