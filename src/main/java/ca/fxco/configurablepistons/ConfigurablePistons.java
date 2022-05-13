package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ConfigurablePistons implements ModInitializer {

    //TODO:
    // Add custom textures - Like: Strong Piston
    // Fix slippery pistons

    public static final String MOD_ID = "configurable-pistons";

    public static Identifier id(String str) {
        return new Identifier(MOD_ID, str);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final Item.Settings CUSTOM_CREATIVE_GROUP = new Item.Settings().group(FabricItemGroupBuilder.build(
            new Identifier("configurable-pistons", "general"),
            () -> new ItemStack(ModBlocks.STRONG_STICKY_PISTON)));

    @Override
    public void onInitialize() {}
}
