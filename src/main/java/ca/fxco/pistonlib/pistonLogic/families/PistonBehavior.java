package ca.fxco.pistonlib.pistonLogic.families;

public class PistonBehavior {

    public static final PistonBehavior DEFAULT = PistonBehavior.Builder().build();

    final boolean verySticky;
    final boolean frontPowered;
    final boolean translocation;
    final boolean slippery;
    final boolean quasi;
    final int pushLimit;
    final float extendingSpeed;
    final float retractingSpeed;
    final boolean canRetractOnExtending;
    final boolean canExtendOnRetracting;

    public PistonBehavior(boolean verySticky, boolean frontPowered, boolean translocation, boolean slippery,
                          boolean quasi, int pushLimit, float extendingSpeed, float retractingSpeed,
                          boolean canRetractOnExtending, boolean canExtendOnRetracting) {
        this.verySticky = verySticky;
        this.frontPowered = frontPowered;
        this.translocation = translocation;
        this.slippery = slippery;
        this.quasi = quasi;
        this.pushLimit = pushLimit;
        this.extendingSpeed = extendingSpeed;
        this.retractingSpeed = retractingSpeed;
        this.canRetractOnExtending = canRetractOnExtending;
        this.canExtendOnRetracting = canExtendOnRetracting;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        boolean verySticky = false;
        boolean frontPowered = false;
        boolean translocation = false;
        boolean slippery = false;
        boolean quasi = true;
        int pushLimit = 12;
        float extendingSpeed = 1;
        float retractingSpeed = 1;
        boolean canRetractOnExtending = true;
        boolean canExtendOnRetracting = false;

        public Builder verySticky() {
            this.verySticky = true;
            return this;
        }

        public Builder frontPowered() {
            this.frontPowered = true;
            return this;
        }

        public Builder translocation() {
            this.translocation = true;
            return this;
        }

        public Builder slippery() {
            this.slippery = true;
            return this;
        }

        public Builder noQuasi() {
            this.quasi = false;
            return this;
        }

        public Builder pushLimit(int pushLimit) {
            this.pushLimit = pushLimit;
            return this;
        }

        public Builder speed(float generalSpeed) {
            this.extendingSpeed = generalSpeed;
            this.retractingSpeed = generalSpeed;
            return this;
        }

        public Builder speed(float extendingSpeed, float retractingSpeed) {
            this.extendingSpeed = extendingSpeed;
            this.retractingSpeed = retractingSpeed;
            return this;
        }

        public Builder canRetractOnExtending(boolean enable) {
            this.canRetractOnExtending = enable;
            return this;
        }

        public Builder canExtendOnRetracting(boolean enable) {
            this.canExtendOnRetracting = enable;
            return this;
        }

        public PistonBehavior build() {
            return new PistonBehavior(verySticky, frontPowered, translocation, slippery, quasi, pushLimit,
                    extendingSpeed, retractingSpeed, canRetractOnExtending, canExtendOnRetracting);
        }
    }
}
