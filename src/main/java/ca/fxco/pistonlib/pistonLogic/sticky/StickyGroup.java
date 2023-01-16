package ca.fxco.pistonlib.pistonLogic.sticky;

import org.jetbrains.annotations.Nullable;

public class StickyGroup {

    @Nullable
    final StickyGroup parent;
    final StickRule rule;

    public StickyGroup(StickRule rule) {
        this(null, rule);
    }

    public StickyGroup(@Nullable StickyGroup parent, StickRule rule) {
        this.parent = parent;
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "StickyGroup{" + StickyGroups.getId(this) + "}";
    }
}
