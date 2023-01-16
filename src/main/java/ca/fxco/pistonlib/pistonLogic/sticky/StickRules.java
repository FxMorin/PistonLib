package ca.fxco.pistonlib.pistonLogic.sticky;

public class StickRules {

    // TODO: some documentation

    public static final StickRule STRICT_SAME = Object::equals;
    public static final StickRule NOT_STRICT_SAME = (group, adjGroup) -> !group.equals(adjGroup);
    public static final StickRule INHERIT_SAME = (group, adjGroup) -> {
        if (adjGroup.equals(group)) return true;
        while(adjGroup.parent != null) {
            if (adjGroup.parent.equals(group)) return true;
            adjGroup = adjGroup.parent;
        }
        return false;
    };
    public static final StickRule NOT_INHERIT_SAME = (group, adjGroup) -> {
        if (adjGroup.equals(group)) return false;
        while(adjGroup.parent != null) {
            if (adjGroup.parent.equals(group)) return false;
            adjGroup = adjGroup.parent;
        }
        return true;
    };

    public static boolean test(StickyGroup group1, StickyGroup group2) {
        if (group1 == null || group2 == null)
            return true;
        return group1.rule.test(group1, group2) && group2.rule.test(group2, group1);
    }
}
