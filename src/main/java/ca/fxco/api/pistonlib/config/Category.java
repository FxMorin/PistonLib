package ca.fxco.api.pistonlib.config;

public enum Category {

    /** Fixes a vanilla bug */
    FIX,

    /** A feature toggle, features are not mechanics */
    FEATURE,

    /** Related to the merging api */
    MERGING,

    /** A core mechanic, disabling this will cause many things to stop working */
    MECHANIC,

    /** An experimental config value, use with caution */
    EXPERIMENTAL,

    /** Work In Progress, this is still missing some features */
    WIP

}
