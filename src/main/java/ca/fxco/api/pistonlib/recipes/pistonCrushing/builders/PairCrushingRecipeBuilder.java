package ca.fxco.api.pistonlib.recipes.pistonCrushing.builders;

import ca.fxco.pistonlib.base.ModRecipeSerializers;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PairCrushingRecipeBuilder implements RecipeBuilder {

    protected final Ingredient first;
    protected final Ingredient second;
    protected final Item result;
    @Nullable
    protected String group;

    public PairCrushingRecipeBuilder(Ingredient first, Ingredient second, ItemLike itemLike) {
        this.first = first;
        this.second = second;
        this.result = itemLike.asItem();
    }

    public static PairCrushingRecipeBuilder crushing(Ingredient first, Ingredient second, ItemLike itemLike) {
        return new PairCrushingRecipeBuilder(first, second, itemLike);
    }

    @Override
    public PairCrushingRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        // Crushing recipes don't have advancements
        return this;
    }

    public PairCrushingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        consumer.accept(new PairCrushingRecipeBuilder.Result(resourceLocation, this.group == null ? "" : this.group, this.first, this.second, this.result));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient first;
        private final Ingredient second;
        private final Item result;

        public Result(ResourceLocation resourceLocation, String group, Ingredient first, Ingredient second, Item item) {
            this.id = resourceLocation;
            this.group = group;
            this.first = first;
            this.second = second;
            this.result = item;
        }

        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }

            jsonObject.add("first", this.first.toJson());
            jsonObject.add("second", this.second.toJson());
            jsonObject.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.PAIR_PISTON_CRUSHING;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
