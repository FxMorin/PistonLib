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

    @ConfigValue(
            desc = "Allows the auto crafting table to be movable",
            keyword = {"auto", "crafting", "moving"},
            category = Category.FEATURE
    )
    public static boolean movableAutoCraftingBlock = true;

    @ConfigValue(
            desc = "Allow pistons to pull blocks out of slots other than the output slot",
            keyword = {"auto", "crafting", "extract"},
            category = Category.FEATURE
    )
    public static boolean extractBlocksFromAutoCrafting = true;

    @ConfigValue(
            desc = "Continues ticking furnace type blocks as they are moving",
            keyword = {"ticking", "furnace", "cooking"},
            category = Category.FEATURE
    )
    public static boolean cookWhileMoving = true;


    // ===============
    //    Mechanics
    // ===============

    @ConfigValue(
            desc = "Toggle the strong sticky type block dropping mechanic where all strong sticky types block drop together",
            keyword = {"block", "dropping"},
            category = Category.MECHANIC
    )
    public static boolean strongBlockDropping = true;

    @ConfigValue(
            desc = "All blocks now block drop together, `strongBlockDropping` does nothing if this is true",
            keyword = {"block", "dropping"},
            category = Category.MECHANIC
    )
    public static boolean combinedBlockDropping = true;

    @ConfigValue(
            desc = "Toggle the merging API, this will prevent all merging-based features from working!",
            keyword = {"merging", "api"},
            category = {Category.MECHANIC, Category.MERGING}
    )
    public static boolean mergingApi = true;

    @ConfigValue(
            desc = "Toggle the ticking API, this will prevent all ticking-based features from working!",
            keyword = {"ticking", "api"},
            category = Category.MECHANIC
    )
    public static boolean tickingApi = true;


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
