package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModScreens {

    static {
        MenuScreens.register(ModMenus.AUTO_CRAFTING, AutoCraftingScreen::new);
    }

    public static void boostrap() { }
}
