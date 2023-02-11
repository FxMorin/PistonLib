package ca.fxco.pistonlib.base;

import ca.fxco.api.pistonlib.recipes.PistonCrushingRecipe;
import ca.fxco.pistonlib.PistonLib;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {

    public static final RecipeType<PistonCrushingRecipe> PISTON_CRUSHING = register("piston_crushing");

    static <T extends Recipe<?>> RecipeType<T> register(String string) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, PistonLib.id(string), new RecipeType<T>() {
            public String toString() {
                return string;
            }
        });
    }

    public static void boostrap() { }
}
