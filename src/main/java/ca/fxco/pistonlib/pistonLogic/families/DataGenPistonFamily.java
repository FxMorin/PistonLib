package ca.fxco.pistonlib.pistonLogic.families;

import lombok.Getter;

public class DataGenPistonFamily extends PistonFamily {

    @Getter
    private final boolean customTextures;

    public DataGenPistonFamily(PistonBehavior behavior) {
        this(behavior, true);
    }

    public DataGenPistonFamily(PistonBehavior behavior, boolean hasCustomTextures) {
        super(behavior);

        this.customTextures = hasCustomTextures;
    }
}
