package ca.fxco.api.pistonlib.recipes.pistonCrushing;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.pistonlib.base.ModRecipeSerializers;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * You also specify what block you must be crushed against for this to work
 */
public class SingleCrushingAgainstRecipe extends SingleCrushingRecipe {

    protected final @NotNull Block againstBlock;

    public SingleCrushingAgainstRecipe(ResourceLocation id, String string, Ingredient ingredient, ItemStack result, @NotNull Block againstBlock) {
        super(id, string, ingredient, result);
        this.againstBlock = againstBlock;
    }

    @Override
    public boolean matches(CrushingContainer container, Level level) {
        return container.getAgainstBlock() == againstBlock && this.ingredient.test(container.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SINGLE_AGAINST_PISTON_CRUSHING;
    }

    public static class Serializer<T extends SingleCrushingAgainstRecipe> implements RecipeSerializer<T> {
        final SingleCrushingAgainstRecipe.Serializer.SingleItemMaker<T> factory;

        public Serializer(SingleCrushingAgainstRecipe.Serializer.SingleItemMaker<T> singleItemMaker) {
            this.factory = singleItemMaker;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            }

            String result = GsonHelper.getAsString(jsonObject, "result");
            int i = GsonHelper.getAsInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(result)), i);
            String against = GsonHelper.getAsString(jsonObject, "against");
            Block againstBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(against));
            return this.factory.create(id, string, ingredient, itemStack, againstBlock);
        }

        public T fromNetwork(ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
            String group = friendlyByteBuf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack result = friendlyByteBuf.readItem();
            Block againstBlock = BuiltInRegistries.BLOCK.get(new ResourceLocation(friendlyByteBuf.readUtf()));
            return this.factory.create(id, group, ingredient, result, againstBlock);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T singleCrushingAgainstRecipe) {
            friendlyByteBuf.writeUtf(singleCrushingAgainstRecipe.group);
            singleCrushingAgainstRecipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(singleCrushingAgainstRecipe.result);
            friendlyByteBuf.writeUtf(BuiltInRegistries.BLOCK.getKey(singleCrushingAgainstRecipe.againstBlock).toString());
        }

        public interface SingleItemMaker<T extends SingleCrushingAgainstRecipe> {
            T create(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, Block againstBlock);
        }
    }
}
