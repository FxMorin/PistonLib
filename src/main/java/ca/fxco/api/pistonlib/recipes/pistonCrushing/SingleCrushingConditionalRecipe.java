package ca.fxco.api.pistonlib.recipes.pistonCrushing;

import ca.fxco.api.pistonlib.containers.CrushingContainer;
import ca.fxco.pistonlib.base.ModRecipeSerializers;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SingleCrushingConditionalRecipe extends SingleCrushingRecipe {

    protected final Condition condition;
    protected final Object data;

    public SingleCrushingConditionalRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, Condition condition, Object data) {
        super(id, group, ingredient, result);
        this.condition = condition;
        this.data = data;
    }

    @Override
    public boolean matches(CrushingContainer container, Level level) {
        return matchesCondition(condition, data, container.getAgainstBlock()) &&
                this.ingredient.test(container.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SINGLE_CONDITIONAL_PISTON_CRUSHING;
    }

    private static boolean matchesCondition(Condition condition, Object data, Block block) {
        if (block == null) {
            return false;
        }
        return switch (condition) {
            case EQUALS -> data.getClass() == block.getClass();
            case INSTANCEOF -> data.getClass().isInstance(block);
            case NAME_CONTAINS -> block.getDescriptionId().contains((String) data);
            case NAME_REGEX -> block.getDescriptionId().matches((String) data);
            case HIGHER_DESTROY_TIME -> block.defaultDestroyTime() > (Float)data;
            case LOWER_DESTROY_TIME -> block.defaultDestroyTime() < (Float)data;
            case HIGHER_RESISTANCE -> block.getExplosionResistance() > (Float)data;
            case LOWER_RESISTANCE -> block.getExplosionResistance() < (Float)data;
        };
    }

    public static class Serializer<T extends SingleCrushingConditionalRecipe> implements RecipeSerializer<T> {
        final SingleCrushingConditionalRecipe.Serializer.SingleItemMaker<T> factory;

        public Serializer(SingleCrushingConditionalRecipe.Serializer.SingleItemMaker<T> singleItemMaker) {
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
            Pair<Condition, Object> data = Condition.readCondition(jsonObject);
            return this.factory.create(id, string, ingredient, itemStack, data.left(), data.right());
        }

        public T fromNetwork(ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
            String group = friendlyByteBuf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack result = friendlyByteBuf.readItem();
            Pair<Condition, Object> data = Condition.readCondition(friendlyByteBuf);
            return this.factory.create(id, group, ingredient, result, data.left(), data.right());
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T singleCrushingRecipe) {
            friendlyByteBuf.writeUtf(singleCrushingRecipe.group);
            singleCrushingRecipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(singleCrushingRecipe.result);
            singleCrushingRecipe.condition.writeCondition(friendlyByteBuf, singleCrushingRecipe.data);
        }

        public interface SingleItemMaker<T extends SingleCrushingConditionalRecipe> {
            T create(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, Condition condition, Object obj);
        }
    }

    private static final BiConsumer<FriendlyByteBuf, Object> floatByteSerializer = (byteBuf, data) -> byteBuf.writeFloat((Float) data);
    private static final BiConsumer<JsonObject, Object> floatJsonSerializer = (json, data) -> json.addProperty("data", (Float) data);
    private static final Function<FriendlyByteBuf, Object> floatByteDeserializer = FriendlyByteBuf::readFloat;
    private static final Function<JsonObject, Object> floatJsonDeserializer = json -> json.get("data").getAsFloat();

    private static final BiConsumer<FriendlyByteBuf, Object> stringByteSerializer = (byteBuf, data) -> byteBuf.writeUtf((String) data);
    private static final BiConsumer<JsonObject, Object> stringJsonSerializer = (json, data) -> json.addProperty("data", (String) data);
    private static final Function<FriendlyByteBuf, Object> stringByteDeserializer = FriendlyByteBuf::readUtf;
    private static final Function<JsonObject, Object> stringJsonDeserializer = json -> json.get("data").getAsString();

    private static final BiConsumer<FriendlyByteBuf, Object> blockByteSerializer = (byteBuf, data) -> byteBuf.writeUtf(BuiltInRegistries.BLOCK.getKey((Block) data).toString());
    private static final BiConsumer<JsonObject, Object> blockJsonSerializer = (json, data) -> json.addProperty("data", BuiltInRegistries.BLOCK.getKey((Block) data).toString());
    private static final Function<FriendlyByteBuf, Object> blockByteDeserializer = byteBuf -> BuiltInRegistries.BLOCK.get(new ResourceLocation(byteBuf.readUtf()));
    private static final Function<JsonObject, Object> blockJsonDeserializer = json -> BuiltInRegistries.BLOCK.get(new ResourceLocation(json.get("data").getAsString()));

    public enum Condition {

        EQUALS("EQ",
                blockByteSerializer,
                blockJsonSerializer,
                blockByteDeserializer,
                blockJsonDeserializer
        ),
        INSTANCEOF("IO",
                blockByteSerializer,
                blockJsonSerializer,
                blockByteDeserializer,
                blockJsonDeserializer
        ),
        NAME_CONTAINS("NC",
                stringByteSerializer,
                stringJsonSerializer,
                stringByteDeserializer,
                stringJsonDeserializer
        ),
        NAME_REGEX("NR",
                stringByteSerializer,
                stringJsonSerializer,
                stringByteDeserializer,
                stringJsonDeserializer
        ),
        HIGHER_DESTROY_TIME("HT",
                floatByteSerializer,
                floatJsonSerializer,
                floatByteDeserializer,
                floatJsonDeserializer
        ),
        LOWER_DESTROY_TIME("LT",
                floatByteSerializer,
                floatJsonSerializer,
                floatByteDeserializer,
                floatJsonDeserializer
        ),
        HIGHER_RESISTANCE("HR",
                floatByteSerializer,
                floatJsonSerializer,
                floatByteDeserializer,
                floatJsonDeserializer
        ),
        LOWER_RESISTANCE("LR",
                floatByteSerializer,
                floatJsonSerializer,
                floatByteDeserializer,
                floatJsonDeserializer
        );

        private final String id;
        private final BiConsumer<FriendlyByteBuf, Object> byteSerializer;
        private final BiConsumer<JsonObject, Object> jsonSerializer;
        private final Function<FriendlyByteBuf, Object> byteDeserializer;
        private final Function<JsonObject, Object> jsonDeserializer;

        Condition(String id, BiConsumer<FriendlyByteBuf, Object> byteSerializer,
                  BiConsumer<JsonObject, Object> jsonSerializer,
                  Function<FriendlyByteBuf, Object> byteDeserializer,
                  Function<JsonObject, Object> jsonDeserializer) {
            this.id = id;
            this.byteSerializer = byteSerializer;
            this.jsonSerializer = jsonSerializer;
            this.byteDeserializer = byteDeserializer;
            this.jsonDeserializer = jsonDeserializer;
        }

        public void writeCondition(FriendlyByteBuf byteBuf, Object data) {
            byteBuf.writeUtf(this.id);
            this.byteSerializer.accept(byteBuf, data);
        }

        public void writeCondition(JsonObject json, Object data) {
            json.addProperty("condition", this.id);
            this.jsonSerializer.accept(json, data);
        }

        public static Pair<Condition, Object> readCondition(FriendlyByteBuf byteBuf) {
            String conditionTag = byteBuf.readUtf();
            for (Condition condition : Condition.values()) {
                if (Objects.equals(condition.id, conditionTag)) {
                    return Pair.of(condition, condition.byteDeserializer.apply(byteBuf));
                }
            }
            return null;
        }

        public static Pair<Condition, Object> readCondition(JsonObject json) {
            String conditionTag = json.get("condition").getAsString();
            for (Condition condition : Condition.values()) {
                if (Objects.equals(condition.id, conditionTag)) {
                    return Pair.of(condition, condition.jsonDeserializer.apply(json));
                }
            }
            return null;
        }
    }
}
