package ca.fxco.pistonlib;

import java.util.function.Consumer;

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
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

import net.minecraft.resources.ResourceLocation;

public class PistonLib implements ModInitializer, PistonLibInitializer {

    public static final String MOD_ID = "pistonlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    @Override
    public void onInitialize() {
        ModRegistries.bootstrap();

        initialize(PistonLibInitializer::registerPistonFamilies);
        initialize(PistonLibInitializer::registerStickyGroups);
        initialize(PistonLibInitializer::bootstrap);

        ModPistonFamilies.validate();
        ModStickyGroups.validate();
    }

    @Override
    public void registerPistonFamilies() {
        ModPistonFamilies.bootstrap();
    }

    @Override
    public void registerStickyGroups() {
        ModStickyGroups.bootstrap();
    }

    @Override
    public void bootstrap() {
        ModBlocks.bootstrap();
        ModBlockEntities.bootstrap();
        ModItems.boostrap();
        ModCreativeModeTabs.bootstrap();
    }

    private void initialize(Consumer<PistonLibInitializer> invoker) {
        EntrypointUtils.invoke(MOD_ID, PistonLibInitializer.class, invoker);
    }
}
