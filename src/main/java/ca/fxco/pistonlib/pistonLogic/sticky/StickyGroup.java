package ca.fxco.pistonlib.pistonLogic.sticky;

import ca.fxco.api.pistonlib.pistonLogic.sticky.StickRule;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModStickyGroups;

@Getter
public class StickyGroup {

    private final @Nullable StickyGroup parent;
    private final StickRule stickRule;

    public StickyGroup(StickRule stickRule) {
        this(null, stickRule);
    }

    public StickyGroup(@Nullable StickyGroup parent, StickRule stickRule) {
        this.parent = parent;
        this.stickRule = stickRule;
    }

    @Override
    public String toString() {
        return "StickyGroup{" + ModStickyGroups.getId(this) + "}";
    }

    public boolean test(StickyGroup stickyGroup) {
        return this.stickRule.test(this, stickyGroup);
    }
}
