package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.helpers.PistonFamily;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class Registerer {
    // TODO: Remove this, its currently a work around cause im too lazy
    public static final HashMap<String, PistonFamily> pistonFamilies = new HashMap<>();

    // Tags
    public static final TagKey<Block> PISTONS = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("pistons"));
    public static final TagKey<Block> MOVING_PISTONS = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("moving_pistons"));
    public static final TagKey<Block> UNPUSHABLE = TagKey.of(Registry.BLOCK_KEY,
            ConfigurablePistons.id("unpushable"));


}
