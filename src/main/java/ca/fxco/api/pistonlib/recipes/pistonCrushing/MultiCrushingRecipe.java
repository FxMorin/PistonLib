package ca.fxco.api.pistonlib.recipes.pistonCrushing;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.api.pistonlib.recipes.PistonCrushingRecipe;
import ca.fxco.pistonlib.base.ModRecipeSerializers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MultiCrushingRecipe implements PistonCrushingRecipe {

    private final ResourceLocation id;
    final String group;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;

    public MultiCrushingRecipe(ResourceLocation id, String string, ItemStack result, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.group = string;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CrushingContainer container, Level level) {
        StackedContents stackedContents = new StackedContents();
        int i = 0;

        for(int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
                ++i;
                stackedContents.accountStack(itemStack, 1);
            }
        }

        return i == this.ingredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(CrushingContainer container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MULTI_PISTON_CRUSHING;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public static class Serializer implements RecipeSerializer<MultiCrushingRecipe> {
        public MultiCrushingRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for multi crushing recipe");
            } else if (ingredients.size() > 16) {
                throw new JsonParseException("Too many ingredients for multi crushing recipe");
            } else {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                return new MultiCrushingRecipe(id, string, result, ingredients);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for(int i = 0; i < jsonArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
                if (!ingredient.isEmpty()) {
                    ingredients.add(ingredient);
                }
            }
            return ingredients;
        }

        public MultiCrushingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
            String string = friendlyByteBuf.readUtf();
            int i = friendlyByteBuf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(friendlyByteBuf));
            }

            ItemStack result = friendlyByteBuf.readItem();
            return new MultiCrushingRecipe(id, string, result, ingredients);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, MultiCrushingRecipe multiCrushingRecipe) {
            friendlyByteBuf.writeUtf(multiCrushingRecipe.group);
            friendlyByteBuf.writeVarInt(multiCrushingRecipe.ingredients.size());

            for(Ingredient ingredient : multiCrushingRecipe.ingredients) {
                ingredient.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeItem(multiCrushingRecipe.result);
        }
    }
}
