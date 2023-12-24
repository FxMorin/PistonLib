package ca.fxco.pistonlib;

import java.util.function.Consumer;

import ca.fxco.pistonlib.config.ConfigManager;
import ca.fxco.pistonlib.base.*;
import ca.fxco.pistonlib.network.PLNetwork;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

import net.minecraft.resources.ResourceLocation;

public class PistonLib implements ModInitializer, PistonLibInitializer {

    public static final String MOD_ID = "pistonlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean DATAGEN_ACTIVE = System.getProperty("fabric-api.datagen") != null;

    @Getter
    private static final ConfigManager configManager = new ConfigManager(MOD_ID);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModRegistries.bootstrap();

        initialize(PistonLibInitializer::registerPistonFamilies);
        initialize(PistonLibInitializer::registerStickyGroups);
        initialize(PistonLibInitializer::bootstrap);

        ModPistonFamilies.validate();
        ModStickyGroups.validate();

        PLNetwork.initialize();

        configManager.loadConfigClass(PistonLibConfig.class);
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
        ModMenus.boostrap();
        ModScreens.boostrap();
    }

    private void initialize(Consumer<PistonLibInitializer> invoker) {
        EntrypointUtils.invoke(MOD_ID, PistonLibInitializer.class, invoker);
    }
}
