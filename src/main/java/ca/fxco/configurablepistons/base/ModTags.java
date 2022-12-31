package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.ConfigurablePistons;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class ModTags {
    // Tags
    public static final TagKey<Block> PISTONS = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("pistons"));
    public static final TagKey<Block> MOVING_PISTONS = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("moving_pistons"));
    public static final TagKey<Block> UNPUSHABLE = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("unpushable"));
    public static final TagKey<Block> SLIPPERY_IGNORE_BLOCKS = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("slippery_ignore_blocks"));
    public static final TagKey<Block> SLIPPERY_TRANSPARENT_BLOCKS = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("slippery_transparent_blocks"));
    public static final TagKey<Block> SLIPPERY_BLOCKS = TagKey.of(Registries.BLOCK.getKey(), ConfigurablePistons.id("slippery_blocks"));
}
