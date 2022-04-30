package ca.fxco.configurablepistons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ConfigurablePistons implements ModInitializer {

    //TODO: OMG Stop the jank
    // Move the client initializer out of here ffs
    // Move block & item intializer out of this class
    // Stop hurting yourself by writing initialization like that
    // Stop reinitializing the freckin predicates
    // Generate the model json files automatically, currently all using the basic model
    // Implement custom textures
    // Stop Bad

    public static final String MOD_ID = "configurable-pistons";

    public static Identifier id(String str) {
        return new Identifier(MOD_ID, str);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final ItemGroup CUSTOM_CREATIVE_GROUP = FabricItemGroupBuilder.build(
            new Identifier("configurable-pistons", "general"),
            () -> new ItemStack(Blocks.STICKY_PISTON));

    @Override
    public void onInitialize() {}
}
