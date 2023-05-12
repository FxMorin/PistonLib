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
            more = "Requires `pistonStructureGrouping`",
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

    @ConfigValue(
            desc = "Group the piston structure into a single ticking block entity, allowing tons of optimizations",
            more = {"Disabling this mechanic also prevents multiple features from working. Such as being able to change the direction mid-way through pushing without block dropping", // I know, game changer xD
                    "This also allows blocks to cull against other blocks in piston structures!"},
            keyword = {"group", "structure"},
            category = {Category.MECHANIC, Category.OPTIMIZATION}
    )
    public static boolean pistonStructureGrouping = true;


    // ===========
    //    Fixes
    // ===========

    @ConfigValue(
            desc = "Fixes pistons pushing entities 0.01 too far",
            more = "The value may not be 0.01 for pistons with different speeds",
            keyword = {"entity", "collision"},
            category = Category.FIX
    )
    public static boolean pistonsPushTooFarFix = true;

    @ConfigValue(
            desc = "Fixes piston progress not being saved correctly, cause some pistons to get out of sync",
            keyword = {"progress", "sync"},
            category = Category.FIX
    )
    public static boolean pistonProgressFix = true;

    @ConfigValue(
            desc = "Fixes tnt duping using pistons",
            more = "This does also fix some other edge cases with modded blocks that behave the same when powered",
            keyword = {"tnt", "duping"},
            category = Category.FIX
    )
    public static boolean tntDupingFix = false;

    @ConfigValue(
            desc = "Fixes tnt duping using pistons",
            more = "This does also fix some other edge cases with modded blocks that behave the same when powered",
            keyword = {"tnt", "duping"},
            category = Category.FIX
    )
    public static WaterloggedState pistonsPushWaterloggedBlocks = WaterloggedState.VANILLA;

    @ConfigValue(
            desc = "Fixes being able to make and use Headless Pistons",
            keyword = {"headless"},
            fixes = 27056,
            category = Category.FIX
    )
    public static boolean headlessPistonFix = true;

    @ConfigValue(
            desc = "Fixes Breaking blocks that should not be able to be broken using headless pistons",
            more = "Illegal blocks are any blocks that have a hardness value of -1.0F",
            keyword = {"headless", "illegal"},
            fixes = 188220,
            category = Category.FIX
    )
    public static boolean illegalBreakingFix = true;

    @ConfigValue(
            desc = "Fixes pistons pulling/pushing blocks using a hashmap causing order to be locational",
            keyword = {"locational", "update", "order"},
            fixes = 233420,
            category = Category.FIX
    )
    public static boolean locationalUpdateOrderFix = true;

    @ConfigValue(
            desc = "Fixes pistons being able to push blocks outside of the world border",
            keyword = {"world border"},
            fixes = 82010,
            category = Category.FIX
    )
    public static boolean pushThroughWorldBorderFix = true;

    @ConfigValue(
            desc = "Fixes mobs being able to spawn on moving pistons",
            more = "Only works on PistonLib pistons, not vanilla pistons",
            keyword = {"mob","spawning"},
            fixes = 163978,
            category = Category.FIX
    )
    public static boolean mobsSpawnOnMovingPistonsFix = true;


    public enum WaterloggedState {
        NONE,
        VANILLA,
        ALL
    }
}
