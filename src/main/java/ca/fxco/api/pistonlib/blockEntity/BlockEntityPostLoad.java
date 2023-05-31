package ca.fxco.api.pistonlib.blockEntity;

/**
 * If a block entity implements this interface. They will be run once, before there first tick
 */
public interface BlockEntityPostLoad {

    boolean pl$shouldPostLoad();

    /**
     * This only gets fired once, before your first tick!
     * Be very very careful with what you do in here!
     */
    void pl$onPostLoad();

}
