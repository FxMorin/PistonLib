package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurablePistons implements ModInitializer {

    //TODO:
    // Add custom textures - Like: Strong Piston
    // Fix Long Pistons & Piston Arms
    // Add support for Carpet & Carpet-Fixes so I can check if carpet-fixes are enabled in order to remove hackfixes
    // Add actual logger support

    public static final String MOD_ID = "configurable-pistons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String str) {
        return new Identifier(MOD_ID, str);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final Item.Settings CUSTOM_CREATIVE_GROUP = new Item.Settings().group(FabricItemGroupBuilder.build(
            new Identifier("configurable-pistons", "general"),
            () -> new ItemStack(ModBlocks.STRONG_STICKY_PISTON)));

    @Override
    public void onInitialize() {
        // Don't mind these, they just make sure the classes are called at the right time
        ModBlocks.order();
        ModBlockEntities.order();
    }
}
