package ca.fxco.pistonlib.pistonLogic.sticky;

import ca.fxco.api.pistonlib.pistonLogic.sticky.StickRule;

public class StickRules {

    // TODO: some documentation

    public static final StickRule STRICT_SAME = Object::equals;
    public static final StickRule NOT_STRICT_SAME = (group, adjGroup) -> !group.equals(adjGroup);
    public static final StickRule INHERIT_SAME = (group, adjGroup) -> {
        if (adjGroup.equals(group)) return true;
        while(adjGroup.getParent() != null) {
            if (adjGroup.getParent().equals(group)) return true;
            adjGroup = adjGroup.getParent();
        }
        return false;
    };
    public static final StickRule NOT_INHERIT_SAME = (group, adjGroup) -> {
        if (adjGroup.equals(group)) return false;
        while(adjGroup.getParent() != null) {
            if (adjGroup.getParent().equals(group)) return false;
            adjGroup = adjGroup.getParent();
        }
        return true;
    };

    public static boolean test(StickyGroup group1, StickyGroup group2) {
        if (group1 == null || group2 == null)
            return true;
        return group1.test(group2) && group2.test(group1);
    }
}
