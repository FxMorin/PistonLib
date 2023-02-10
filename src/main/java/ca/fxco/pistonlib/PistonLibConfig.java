package ca.fxco.pistonlib;

import ca.fxco.api.pistonlib.config.ConfigValue;
import ca.fxco.api.pistonlib.config.Category;

public class PistonLibConfig {

    // ===========
    //  Mechanics
    // ===========

    @ConfigValue(
            desc = "Adds a mechanic to create packed ice by mering multiple ice blocks together",
            keyword = {"ice", "merging", "packed"},
            category = {Category.MECHANIC, Category.MERGING}
    )
    public static boolean doIceMerging = true;

    @ConfigValue(
            desc = "Adds a mechanic which allows you to merge signs together",
            keyword = {"sign", "merging"},
            category = {Category.MECHANIC, Category.MERGING}
    )
    public static boolean doSignMerging = true;

    @ConfigValue(
            desc = "Adds a mechanic which allows you to merge slabs together",
            keyword = {"slab", "merging"},
            category = {Category.MECHANIC, Category.MERGING}
    )
    public static boolean doSlabMerging = true;

    // ===========
    //    Fixes
    // ===========

    @ConfigValue(
            desc = "Fixes piston progress not being saved correctly, cause some pistons to get out of sync",
            keyword = {"progress", "sync"},
            category = Category.FIX
    )
    public static boolean pistonProgressFix = true;
}
