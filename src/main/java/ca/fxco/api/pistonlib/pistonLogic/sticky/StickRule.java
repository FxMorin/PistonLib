package ca.fxco.api.pistonlib.pistonLogic.sticky;

import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

@FunctionalInterface
public interface StickRule {

    boolean test(StickyGroup group1, StickyGroup group2);

}
