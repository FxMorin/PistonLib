package ca.fxco.pistonlib.pistonLogic.sticky;

@FunctionalInterface
public interface StickRule {

    boolean test(StickyGroup group1, StickyGroup group2);

}
