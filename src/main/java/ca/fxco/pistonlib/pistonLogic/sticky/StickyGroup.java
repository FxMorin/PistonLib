package ca.fxco.pistonlib.pistonLogic.sticky;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModStickyGroups;

public class StickyGroup {

    @Nullable
    private final StickyGroup parent;
    private final StickRule rule;

    public StickyGroup(StickRule rule) {
        this(null, rule);
    }

    public StickyGroup(@Nullable StickyGroup parent, StickRule rule) {
        this.parent = parent;
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "StickyGroup{" + ModStickyGroups.getId(this) + "}";
    }

    public @Nullable StickyGroup getParent() {
        return this.parent;
    }

    public StickRule getStickRule() {
        return this.rule;
    }
}
