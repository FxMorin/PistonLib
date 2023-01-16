package ca.fxco.pistonlib;

public interface PistonLibInitializer {

    /**
     * Register custom piston families through
     * {@linkplain ca.fxco.pistonlib.base.ModPistonFamilies#register}
     */
    void registerPistonFamilies();

    /**
     * Register custom sticky groups through
     * {@linkplain ca.fxco.pistonlib.base.ModStickyGroups#register}
     */
    void registerStickyGroups();

    /**
     * Initialize custom registries, blocks, items, etc.
     */
    void bootstrap();

}
