package ca.fxco.api.pistonlib.recipes.pistonCrushing;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.api.pistonlib.recipes.PistonCrushingRecipe;
import ca.fxco.pistonlib.base.ModRecipeSerializers;
import ca.fxco.pistonlib.base.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class SingleCrushingRecipe implements PistonCrushingRecipe {

    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final String group;

    public SingleCrushingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(CrushingContainer container, Level level) {
        return this.ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(CrushingContainer container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
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
        return ModRecipeSerializers.SINGLE_PISTON_CRUSHING;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PISTON_CRUSHING;
    }

    public static class Serializer<T extends SingleCrushingRecipe> implements RecipeSerializer<T> {
        final SingleCrushingRecipe.Serializer.SingleItemMaker<T> factory;

        public Serializer(SingleCrushingRecipe.Serializer.SingleItemMaker<T> singleItemMaker) {
            this.factory = singleItemMaker;
        }

        public T fromJson(ResourceLocation id, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            }

            String string2 = GsonHelper.getAsString(jsonObject, "result");
            int i = GsonHelper.getAsInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(string2)), i);
            return this.factory.create(id, string, ingredient, itemStack);
        }

        public T fromNetwork(ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
            String group = friendlyByteBuf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack result = friendlyByteBuf.readItem();
            return this.factory.create(id, group, ingredient, result);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T singleCrushingRecipe) {
            friendlyByteBuf.writeUtf(singleCrushingRecipe.group);
            singleCrushingRecipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(singleCrushingRecipe.result);
        }

        public interface SingleItemMaker<T extends SingleCrushingRecipe> {
            T create(ResourceLocation id, String group, Ingredient ingredient, ItemStack result);
        }
    }
}
