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
     * 
     * It is recommended to register custom moving block entity types through
     * {@linkplain ca.fxco.pistonlib.base.ModBlockEntities#register}
     */
    void bootstrap();

}
