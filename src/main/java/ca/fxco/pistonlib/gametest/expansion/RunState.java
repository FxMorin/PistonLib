package ca.fxco.pistonlib.gametest.expansion;

public @interface RunState {

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
