package ca.fxco.api.pistonlib.recipes.pistonCrushing;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.api.pistonlib.recipes.PistonCrushingRecipe;
import ca.fxco.pistonlib.base.ModRecipeSerializers;
import ca.fxco.pistonlib.base.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * A piston crushing recipe that simply merges two different items together.
 * Container is only 2 slots
 */
public class PairCrushingRecipe implements PistonCrushingRecipe {

    final Ingredient first;
    final Ingredient second;
    final ItemStack result;
    private final ResourceLocation id;

    public PairCrushingRecipe(ResourceLocation id, Ingredient first, Ingredient second, ItemStack result) {
        this.id = id;
        this.first = first;
        this.second = second;
        this.result = result;
    }

    @Override
    public boolean matches(CrushingContainer container, Level level) {
        return this.first.test(container.getItem(0)) && this.second.test(container.getItem(1));
    }

    @Override
    public ItemStack assemble(CrushingContainer container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STICKY_PISTON);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PAIR_PISTON_CRUSHING;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PISTON_CRUSHING;
    }

    @Override
    public boolean isIncomplete() {
        return this.first.getItems().length == 0 || this.second.getItems().length == 0;
    }

    public static class Serializer implements RecipeSerializer<PairCrushingRecipe> {
        public PairCrushingRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            Ingredient first = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "first"));
            Ingredient second = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "second"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            return new PairCrushingRecipe(id, first, second, result);
        }

        public PairCrushingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
            Ingredient first = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient second = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack result = friendlyByteBuf.readItem();
            return new PairCrushingRecipe(id, first, second, result);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PairCrushingRecipe pairCrushingRecipe) {
            pairCrushingRecipe.first.toNetwork(friendlyByteBuf);
            pairCrushingRecipe.second.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(pairCrushingRecipe.result);
        }
    }
}
