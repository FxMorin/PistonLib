package ca.fxco.pistonlib.pistonLogic.families;

public class PistonBehavior {

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

    public PistonBehavior verySticky() {
        this.verySticky = true;
        return this;
    }

    public PistonBehavior frontPowered() {
        this.frontPowered = true;
        return this;
    }

    public PistonBehavior translocation() {
        this.translocation = true;
        return this;
    }

    public PistonBehavior slippery() {
        this.slippery = true;
        return this;
    }

    public PistonBehavior noQuasi() {
        this.quasi = false;
        return this;
    }

    public PistonBehavior pushLimit(int pushLimit) {
        this.pushLimit = pushLimit;
        return this;
    }

    public PistonBehavior speed(float generalSpeed) {
        this.extendingSpeed = generalSpeed;
        this.retractingSpeed = generalSpeed;
        return this;
    }

    public PistonBehavior speed(float extendingSpeed, float retractingSpeed) {
        this.extendingSpeed = extendingSpeed;
        this.retractingSpeed = retractingSpeed;
        return this;
    }

    public PistonBehavior canRetractOnExtending(boolean enable) {
        this.canRetractOnExtending = enable;
        return this;
    }

    public PistonBehavior canExtendOnRetracting(boolean enable) {
        this.canExtendOnRetracting = enable;
        return this;
    }
}
