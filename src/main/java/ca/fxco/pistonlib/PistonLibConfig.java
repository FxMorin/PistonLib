package ca.fxco.pistonlib;

import ca.fxco.api.pistonlib.config.ConfigValue;
import ca.fxco.api.pistonlib.config.Category;

public class PistonLibConfig {

    // ==============
    //    Features
    // ==============

    @ConfigValue(
            desc = "Adds a feature to create packed ice by mering multiple ice blocks together",
            keyword = {"ice", "merging", "packed"},
            category = {Category.FEATURE, Category.MERGING}
    )
    public static boolean doIceMerging = true;

    @ConfigValue(
            desc = "Adds a feature which allows you to merge signs together",
            keyword = {"sign", "merging"},
            category = {Category.FEATURE, Category.MERGING}
    )
    public static boolean doSignMerging = true;

    @ConfigValue(
            desc = "Adds a feature which allows you to merge slabs together",
            keyword = {"slab", "merging"},
            category = {Category.FEATURE, Category.MERGING}
    )
    public static boolean doSlabMerging = true;

    // TODO: Hide item from the creative inventory when disbled, since all crafting blocks placed when disabled won't have a block entity
    @ConfigValue(
            desc = "Toggle the auto crafting block feature. The block will still exist it just wont work if disabled",
            keyword = {"auto", "crafting"},
            category = Category.FEATURE
    )
    public static boolean autoCraftingBlock = true;

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
