package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.ConfigurablePistons;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> PISTONS = createBlockTag("pistons");
    public static final TagKey<Block> MOVING_PISTONS = createBlockTag("moving_pistons");
    public static final TagKey<Block> UNPUSHABLE = createBlockTag("unpushable");
    public static final TagKey<Block> SLIPPERY_IGNORE_BLOCKS = createBlockTag("slippery_ignore_blocks");
    public static final TagKey<Block> SLIPPERY_TRANSPARENT_BLOCKS = createBlockTag("slippery_transparent_blocks");
    public static final TagKey<Block> SLIPPERY_BLOCKS = createBlockTag("slippery_blocks");

    private static TagKey<Block> createBlockTag(String path) {
        return createBlockTag(ConfigurablePistons.id(path));
    }

    public static TagKey<Block> createBlockTag(ResourceLocation id) {
        return TagKey.create(Registries.BLOCK, id);
    }
}
