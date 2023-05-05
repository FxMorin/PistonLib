package ca.fxco.pistonlib.pistonLogic.families;

public class DataGenPistonFamily extends PistonFamily {

    private final boolean customTextures;

    public DataGenPistonFamily(PistonBehavior behavior, boolean hasCustomTextures) {
        super(behavior);

        this.customTextures = hasCustomTextures;
    }

    @Override
    public boolean hasCustomTextures() {
        return customTextures;
    }
}
