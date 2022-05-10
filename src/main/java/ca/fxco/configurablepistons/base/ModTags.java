package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.ConfigurablePistons;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class ModTags {
    // Tags
    public static final TagKey<Block> PISTONS = TagKey.of(Registry.BLOCK_KEY, ConfigurablePistons.id("pistons"));
    public static final TagKey<Block> MOVING_PISTONS = TagKey.of(Registry.BLOCK_KEY, ConfigurablePistons.id("moving_pistons"));
    public static final TagKey<Block> UNPUSHABLE = TagKey.of(Registry.BLOCK_KEY, ConfigurablePistons.id("unpushable"));
    public static final TagKey<Block> STICKY_BLOCKS = TagKey.of(Registry.BLOCK_KEY, ConfigurablePistons.id("sticky_blocks"));
    public static final TagKey<Block> SLIPPERY_IGNORE_BLOCKS = TagKey.of(Registry.BLOCK_KEY, ConfigurablePistons.id("slippery_ignore_blocks"));
}
