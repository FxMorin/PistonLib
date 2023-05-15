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

    /** Extreme means this has a Major impact on how pistons work and will likely break any contraption made without it being on */
    EXTREME,

    /** An experimental config value, use with caution */
    EXPERIMENTAL,

    /** An optimization config value, performance go brrrr */
    OPTIMIZATION,

    /** Work In Progress, this is still missing some features */
    WIP

}
