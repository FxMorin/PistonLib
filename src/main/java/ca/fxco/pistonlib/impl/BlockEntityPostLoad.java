package ca.fxco.pistonlib.impl;

/**
 * If a block entity implements this interface. They will be run once, before there first tick
 */
public interface BlockEntityPostLoad {

    boolean shouldPostLoad();

    /**
     * This only gets fired once, before your first tick!
     * Be very very careful with what you do in here!
     */
    void onPostLoad();

}
