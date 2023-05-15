package ca.fxco.pistonlib.pistonLogic.families;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PistonBehavior {

    public static final PistonBehavior DEFAULT = PistonBehavior.builder().build();

    private final boolean verySticky;
    private final boolean frontPowered;
    private final boolean slippery;
    private final boolean quasi;
    @Builder.Default private final int pushLimit = 12;
    private final float extendingSpeed;
    private final float retractingSpeed;
    @Builder.Default private final boolean retractOnExtending = true;
    private final boolean extendOnRetracting;
    @Builder.Default private final int minLength = 0;
    @Builder.Default private final int maxLength = 1;

    public static class PistonBehaviorBuilder {

        boolean verySticky = false;
        boolean frontPowered = false;
        boolean slippery = false;
        boolean quasi = true;
        float extendingSpeed = 1;
        float retractingSpeed = 1;

        public PistonBehaviorBuilder verySticky() {
            this.verySticky = true;
            return this;
        }

        public PistonBehaviorBuilder frontPowered() {
            this.frontPowered = true;
            return this;
        }

        public PistonBehaviorBuilder slippery() {
            this.slippery = true;
            return this;
        }

        public PistonBehaviorBuilder noQuasi() {
            this.quasi = false;
            return this;
        }

        public PistonBehaviorBuilder speed(float generalSpeed) {
            this.extendingSpeed = generalSpeed;
            this.retractingSpeed = generalSpeed;
            return this;
        }

        public PistonBehaviorBuilder speed(float extendingSpeed, float retractingSpeed) {
            this.extendingSpeed = extendingSpeed;
            this.retractingSpeed = retractingSpeed;
            return this;
        }
    }
}
