package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModCreativeModeTabs {

    public static final CreativeModeTab GENERAL = FabricItemGroup.builder(id("general"))
        .icon(() -> new ItemStack(ModItems.BASIC_STICKY_PISTON))
        .displayItems((featureFlags, output, hasPermissions) -> {
            output.accept(ModItems.PISTON_WAND);
            output.accept(ModItems.HALF_SLIME_BLOCK);
            output.accept(ModItems.HALF_HONEY_BLOCK);
            output.accept(ModItems.HALF_REDSTONE_BLOCK);
            output.accept(ModItems.HALF_OBSIDIAN_BLOCK);
            output.accept(ModItems.HALF_REDSTONE_LAMP_BLOCK);

            output.accept(ModItems.DRAG_BLOCK);
            output.accept(ModItems.STICKYLESS_BLOCK);
            output.accept(ModItems.STICKY_TOP_BLOCK);
            output.accept(ModItems.SLIMY_REDSTONE_BLOCK);
            output.accept(ModItems.ALL_SIDED_OBSERVER);
            output.accept(ModItems.GLUE_BLOCK);
            output.accept(ModItems.POWERED_STICKY_BLOCK);
            output.accept(ModItems.STICKY_CHAIN_BLOCK);
            output.accept(ModItems.AXIS_LOCKED_BLOCK);
            output.accept(ModItems.MOVE_COUNTING_BLOCK);
            output.accept(ModItems.WEAK_REDSTONE_BLOCK);
            output.accept(ModItems.QUASI_BLOCK);

            output.accept(ModItems.SLIPPERY_SLIME_BLOCK);
            output.accept(ModItems.SLIPPERY_REDSTONE_BLOCK);
            output.accept(ModItems.SLIPPERY_STONE_BLOCK);

            output.accept(ModItems.OBSIDIAN_SLAB_BLOCK);
            output.accept(ModItems.OBSIDIAN_STAIR_BLOCK);

            output.accept(ModItems.AUTO_CRAFTING_BLOCK);
        })
        .build();

    public static final CreativeModeTab PISTONS = FabricItemGroup.builder(id("pistons"))
        .icon(() -> new ItemStack(ModItems.STRONG_STICKY_PISTON))
        .displayItems((featureFlags, output, hasPermissions) -> {
            for (PistonFamily family : ModRegistries.PISTON_FAMILY) {
                for (Block base : family.getBases().values()) {
                    output.accept(base);
                }
            }
        })
        .build();

    public static void bootstrap() { }

}
