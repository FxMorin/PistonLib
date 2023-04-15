package ca.fxco.api.pistonlib.recipes.pistonCrushing.builders;

import ca.fxco.api.pistonlib.recipes.pistonCrushing.SingleCrushingConditionalRecipe;
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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SingleCrushingRecipeBuilder implements RecipeBuilder {
    protected final Item result;
    protected final Ingredient ingredient;
    protected final int count;
    @Nullable
    protected String group;
    @Nullable
    protected Block againstBlock;
    @Nullable
    protected SingleCrushingConditionalRecipe.Condition condition;
    @Nullable
    protected Object data;

    public SingleCrushingRecipeBuilder(Ingredient ingredient, ItemLike itemLike, int count) {
        this.ingredient = ingredient;
        this.result = itemLike.asItem();
        this.count = count;
    }

    public static SingleCrushingRecipeBuilder crushing(Ingredient ingredient, ItemLike itemLike) {
        return crushing(ingredient, itemLike, 1);
    }

    public static SingleCrushingRecipeBuilder crushing(Ingredient ingredient, ItemLike itemLike, int count) {
        return new SingleCrushingRecipeBuilder(ingredient, itemLike, count);
    }

    public SingleCrushingRecipeBuilder mustBeAgainst(Block againstBlock) {
        if (this.condition != null) {
            throw new IllegalStateException("You can only use either `mustBeAgainst` or `hasConditional`");
        }
        this.againstBlock = againstBlock;
        return this;
    }

    public SingleCrushingRecipeBuilder hasConditional(SingleCrushingConditionalRecipe.Condition condition, Object data) {
        if (this.againstBlock != null) {
            throw new IllegalStateException("You can only use either `mustBeAgainst` or `hasConditional`");
        }
        this.condition = condition;
        this.data = data;
        return this;
    }

    @Override
    public SingleCrushingRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        // Crushing recipes don't have advancements
        return this;
    }

    public SingleCrushingRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        if (this.againstBlock != null) {
            consumer.accept(new SingleCrushingRecipeBuilder.ResultWithAgainst(resourceLocation, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.againstBlock));
        } else if (this.condition != null) {
            consumer.accept(new SingleCrushingRecipeBuilder.ResultWithCondition(resourceLocation, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.condition, this.data));
        } else {
            consumer.accept(new SingleCrushingRecipeBuilder.Result(resourceLocation, this.group == null ? "" : this.group, this.ingredient, this.result, this.count));
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final int count;

        public Result(ResourceLocation resourceLocation, String group, Ingredient ingredient, Item item, int count) {
            this.id = resourceLocation;
            this.group = group;
            this.ingredient = ingredient;
            this.result = item;
            this.count = count;
        }

        public void serializeRecipeData(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }

            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
            jsonObject.addProperty("count", this.count);
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.SINGLE_PISTON_CRUSHING;
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

    public static class ResultWithAgainst extends Result {

        private final Block againstBlock;

        public ResultWithAgainst(ResourceLocation id, String group, Ingredient ingredient, Item item, int count, Block againstBlock) {
            super(id, group, ingredient, item, count);
            this.againstBlock = againstBlock;
        }

        public void serializeRecipeData(JsonObject jsonObject) {
            super.serializeRecipeData(jsonObject);
            jsonObject.addProperty("against", BuiltInRegistries.BLOCK.getKey(againstBlock).toString());
        }

        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.SINGLE_AGAINST_PISTON_CRUSHING;
        }
    }

    public static class ResultWithCondition extends Result {

        private final SingleCrushingConditionalRecipe.Condition condition;
        private final Object data;

        public ResultWithCondition(ResourceLocation id, String group, Ingredient ingredient, Item item, int count, SingleCrushingConditionalRecipe.Condition condition, Object data) {
            super(id, group, ingredient, item, count);
            this.condition = condition;
            this.data = data;
        }

        public void serializeRecipeData(JsonObject jsonObject) {
            super.serializeRecipeData(jsonObject);
            condition.writeCondition(jsonObject, this.data);
        }

        public RecipeSerializer<?> getType() {
            return ModRecipeSerializers.SINGLE_CONDITIONAL_PISTON_CRUSHING;
        }
    }
}
