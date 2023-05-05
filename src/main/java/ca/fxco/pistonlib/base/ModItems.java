package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.impl.toggle.ToggleableProperties;
import ca.fxco.pistonlib.items.PistonWandItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModItems {

    public static final PistonWandItem PISTON_WAND = register("piston_wand", new PistonWandItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final BlockItem HALF_SLIME_BLOCK = registerBlock(ModBlocks.HALF_SLIME_BLOCK);
    public static final BlockItem HALF_HONEY_BLOCK = registerBlock(ModBlocks.HALF_HONEY_BLOCK);
    public static final BlockItem HALF_REDSTONE_BLOCK = registerBlock(ModBlocks.HALF_REDSTONE_BLOCK);
    public static final BlockItem HALF_OBSIDIAN_BLOCK = registerBlock(ModBlocks.HALF_OBSIDIAN_BLOCK);
    public static final BlockItem HALF_REDSTONE_LAMP_BLOCK = registerBlock(ModBlocks.HALF_REDSTONE_LAMP_BLOCK);

    public static final BlockItem DRAG_BLOCK = registerBlock(ModBlocks.DRAG_BLOCK);
    public static final BlockItem STICKYLESS_BLOCK = registerBlock(ModBlocks.STICKYLESS_BLOCK);
    public static final BlockItem STICKY_TOP_BLOCK = registerBlock(ModBlocks.STICKY_TOP_BLOCK);
    public static final BlockItem SLIMY_REDSTONE_BLOCK = registerBlock(ModBlocks.SLIMY_REDSTONE_BLOCK);
    public static final BlockItem ALL_SIDED_OBSERVER = registerBlock(ModBlocks.ALL_SIDED_OBSERVER);
    public static final BlockItem GLUE_BLOCK = registerBlock(ModBlocks.GLUE_BLOCK);
    public static final BlockItem POWERED_STICKY_BLOCK = registerBlock(ModBlocks.POWERED_STICKY_BLOCK);
    public static final BlockItem STICKY_CHAIN_BLOCK = registerBlock(ModBlocks.STICKY_CHAIN_BLOCK);
    public static final BlockItem AXIS_LOCKED_BLOCK = registerBlock(ModBlocks.AXIS_LOCKED_BLOCK);
    public static final BlockItem MOVE_COUNTING_BLOCK = registerBlock(ModBlocks.MOVE_COUNTING_BLOCK);
    public static final BlockItem WEAK_REDSTONE_BLOCK = registerBlock(ModBlocks.WEAK_REDSTONE_BLOCK);
    public static final BlockItem QUASI_BLOCK = registerBlock(ModBlocks.QUASI_BLOCK);

    public static final BlockItem SLIPPERY_SLIME_BLOCK = registerBlock(ModBlocks.SLIPPERY_SLIME_BLOCK);
    public static final BlockItem SLIPPERY_REDSTONE_BLOCK = registerBlock(ModBlocks.SLIPPERY_REDSTONE_BLOCK);
    public static final BlockItem SLIPPERY_STONE_BLOCK = registerBlock(ModBlocks.SLIPPERY_STONE_BLOCK);

    public static final BlockItem OBSIDIAN_SLAB_BLOCK = registerBlock(ModBlocks.OBSIDIAN_SLAB_BLOCK);
    public static final BlockItem OBSIDIAN_STAIR_BLOCK = registerBlock(ModBlocks.OBSIDIAN_STAIR_BLOCK);

    public static final BlockItem CONFIGURABLE_PISTON = registerBlock(ModBlocks.CONFIGURABLE_PISTON);
    public static final BlockItem CONFIGURABLE_STICKY_PISTON = registerBlock(ModBlocks.CONFIGURABLE_STICKY_PISTON);
    public static final BlockItem BASIC_PISTON = registerBlock(ModBlocks.BASIC_PISTON);
    public static final BlockItem BASIC_STICKY_PISTON = registerBlock(ModBlocks.BASIC_STICKY_PISTON);
    public static final BlockItem LONG_PISTON = registerBlock(ModBlocks.LONG_PISTON);
    public static final BlockItem LONG_STICKY_PISTON = registerBlock(ModBlocks.LONG_STICKY_PISTON);
    public static final BlockItem STALE_PISTON = registerBlock(ModBlocks.STALE_PISTON);
    public static final BlockItem STALE_STICKY_PISTON = registerBlock(ModBlocks.STALE_STICKY_PISTON);
    public static final BlockItem VERY_QUASI_PISTON = registerBlock(ModBlocks.VERY_QUASI_PISTON);
    public static final BlockItem VERY_QUASI_STICKY_PISTON = registerBlock(ModBlocks.VERY_QUASI_STICKY_PISTON);
    public static final BlockItem STRONG_PISTON = registerBlock(ModBlocks.STRONG_PISTON);
    public static final BlockItem STRONG_STICKY_PISTON = registerBlock(ModBlocks.STRONG_STICKY_PISTON);
    public static final BlockItem FAST_PISTON = registerBlock(ModBlocks.FAST_PISTON);
    public static final BlockItem FAST_STICKY_PISTON = registerBlock(ModBlocks.FAST_STICKY_PISTON);
    public static final BlockItem VERY_STICKY_PISTON = registerBlock(ModBlocks.VERY_STICKY_PISTON);
    public static final BlockItem FRONT_POWERED_PISTON = registerBlock(ModBlocks.FRONT_POWERED_PISTON);
    public static final BlockItem FRONT_POWERED_STICKY_PISTON = registerBlock(ModBlocks.FRONT_POWERED_STICKY_PISTON);
    public static final BlockItem SLIPPERY_PISTON = registerBlock(ModBlocks.SLIPPERY_PISTON);
    public static final BlockItem SLIPPERY_STICKY_PISTON = registerBlock(ModBlocks.SLIPPERY_STICKY_PISTON);
    public static final BlockItem SUPER_PISTON = registerBlock(ModBlocks.SUPER_PISTON);
    public static final BlockItem SUPER_STICKY_PISTON = registerBlock(ModBlocks.SUPER_STICKY_PISTON);
    public static final BlockItem MBE_PISTON = registerBlock(ModBlocks.MBE_PISTON);
    public static final BlockItem MBE_STICKY_PISTON = registerBlock(ModBlocks.MBE_STICKY_PISTON);

    public static final BlockItem AUTO_CRAFTING_BLOCK = registerBlock(ModBlocks.AUTO_CRAFTING_BLOCK, ((ToggleableProperties<Item.Properties>)new Item.Properties()).setDisabled(() -> !PistonLibConfig.autoCraftingBlock));

    private static BlockItem registerBlock(Block block) {
        return registerBlock(block, new Item.Properties());
    }

    private static BlockItem registerBlock(Block block, Item.Properties itemProperties) {
        return register(BuiltInRegistries.BLOCK.getKey(block), new BlockItem(block, itemProperties));
    }

    private static <T extends Item> T register(String name, T item) {
        return register(id(name), item);
    }

    private static <T extends Item> T register(ResourceLocation id, T item) {
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void boostrap() { }

}
