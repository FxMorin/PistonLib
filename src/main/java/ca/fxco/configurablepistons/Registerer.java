package ca.fxco.configurablepistons;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class Registerer {
    // Tags
    public static final TagKey<Block> PISTONS = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("pistons"));
    public static final TagKey<Block> MOVING_PISTONS = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("moving_pistons"));
    public static final TagKey<Block> UNPUSHABLE = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("unpushable"));


}
