package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.blocks.autoCraftingBlock.AutoCraftingMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final MenuType<AutoCraftingMenu> AUTO_CRAFTING = register("auto_crafting", AutoCraftingMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String string, MenuType.MenuSupplier<T> menuSupplier) {
        return null;//Registry.register(BuiltInRegistries.MENU, string, new MenuType<>(menuSupplier));
    }

    public static void boostrap() { }
}
