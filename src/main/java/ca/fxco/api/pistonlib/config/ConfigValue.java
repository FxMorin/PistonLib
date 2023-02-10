package ca.fxco.api.pistonlib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that this field is a Config Value
 * All config value fields must be static and not final
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {

    /**
     * The config description, what does this config do?
     */
    String desc() default "";

    /**
     * More information about the config
     */
    String[] more() default {};

    /**
     * List of keywords that fit the config value
     */
    String[] keyword() default {};

    /**
     * The categories that this config value fits into
     */
    Category[] category() default {};

    /**
     * If this config value fixes a vanilla bug, you can set the bug id's it fixes here
     * Just a default mojira id without the `MC-`
     */
    int[] fixes() default {};
}
