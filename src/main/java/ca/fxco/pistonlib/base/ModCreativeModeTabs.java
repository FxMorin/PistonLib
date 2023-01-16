package ca.fxco.pistonlib.base;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModCreativeModeTabs {

    public static final CreativeModeTab GENERAL = FabricItemGroup.builder(id("general"))
        .icon(() -> new ItemStack(ModItems.STRONG_STICKY_PISTON))
        .displayItems((featureFlags, output, hasPermissions) -> {
            // TODO
        })
        .build();

    public static void bootstrap() { }

}
