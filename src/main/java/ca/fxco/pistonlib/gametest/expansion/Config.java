package ca.fxco.pistonlib.gametest.expansion;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Unused currently
public @interface Config {

    /**
     * Strings must be a valid config field names
     */
    String[] value();

    /**
     * Specify what should change about the test when these config values are used.
     */
    GameTestChanges changes() default GameTestChanges.NONE;


    @Getter
    @AllArgsConstructor
    enum GameTestChanges {

        /**
         * Nothing should change
         */
        NONE(false, false),

        /**
         * TestTrigger Blocks will give the opposite result
         */
        FLIP_TRIGGERS(true, false),

        /**
         * CheckState Blocks will flip their FailOnFound state
         */
        FLIP_CHECKS(false, true),

        /**
         * Both `FLIP_TRIGGERS` & `FLIP_CHECKS`
         */
        FLIP_TRIGGERS_CHECKS(true, true);

        private final boolean flipTriggers;
        private final boolean flipChecks;
    }
}
