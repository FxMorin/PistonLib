package ca.fxco.pistonlib.base;

import ca.fxco.api.pistonlib.recipes.pistonCrushing.*;
import ca.fxco.pistonlib.PistonLib;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    public static final RecipeSerializer<SingleCrushingRecipe> SINGLE_PISTON_CRUSHING = register("piston_crushing_single", new SingleCrushingRecipe.Serializer<>(SingleCrushingRecipe::new));
    public static final RecipeSerializer<SingleCrushingAgainstRecipe> SINGLE_AGAINST_PISTON_CRUSHING = register("piston_crushing_single_against", new SingleCrushingAgainstRecipe.Serializer<>(SingleCrushingAgainstRecipe::new));
    public static final RecipeSerializer<SingleCrushingConditionalRecipe> SINGLE_CONDITIONAL_PISTON_CRUSHING = register("piston_crushing_single_conditional", new SingleCrushingConditionalRecipe.Serializer<>(SingleCrushingConditionalRecipe::new));
    public static final RecipeSerializer<PairCrushingRecipe> PAIR_PISTON_CRUSHING = register("piston_crushing_pair", new PairCrushingRecipe.Serializer());
    public static final RecipeSerializer<MultiCrushingRecipe> MULTI_PISTON_CRUSHING = register("piston_crushing_multi", new MultiCrushingRecipe.Serializer());

    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String string, S recipeSerializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, PistonLib.id(string), recipeSerializer);
    }

    public static void boostrap() { }
}
