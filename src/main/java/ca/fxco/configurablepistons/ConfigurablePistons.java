package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurablePistons implements ModInitializer {

    public static final String MOD_ID = "configurable-pistons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String str) {
        return new Identifier(MOD_ID, str);
    }

    public static final boolean PISTON_PROGRESS_FIX = true;

    public static final ItemGroup CUSTOM_CREATIVE_GROUP = FabricItemGroup
            .builder(new Identifier("configurable-pistons", "general"))
            .icon(() -> new ItemStack(ModBlocks.STRONG_STICKY_PISTON))
            .build();

    @Override
    public void onInitialize() {
        // Don't mind these, they just make sure the classes are called at the right time
        ModBlocks.order();
        ModBlockEntities.order();
    }
}
