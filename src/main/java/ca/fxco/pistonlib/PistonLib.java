package ca.fxco.pistonlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModCreativeModeTabs;
import ca.fxco.pistonlib.base.ModItems;
import ca.fxco.pistonlib.base.ModPistonFamilies;
import ca.fxco.pistonlib.base.ModRegistries;
import ca.fxco.pistonlib.base.ModStickyGroups;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PistonLib implements ModInitializer {

    public static final String MOD_ID = "pistonlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final CreativeModeTab CUSTOM_CREATIVE_MODE_TAB = FabricItemGroup.builder(id("general"))
            .icon(() -> new ItemStack(ModBlocks.STRONG_STICKY_PISTON))
            .build();

    @Override
    public void onInitialize() {
        // Don't mind these, they just make sure the classes are called at the right time
        ModRegistries.bootstrap();
        ModPistonFamilies.bootstrap();
        ModStickyGroups.bootstrap();
        ModBlocks.bootstrap();
        ModBlockEntities.bootstrap();
        ModItems.boostrap();
        ModCreativeModeTabs.bootstrap();

        // TODO: ensure that other mods register their custom pistons
        // before locking the piston families and sticky groups.
        ModPistonFamilies.validate();
        ModStickyGroups.validate();
    }
}
