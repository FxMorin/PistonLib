package ca.fxco.configurablepistons.pistonLogic.families;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Map;

public class PistonFamilies {
    private static final Map<Block, PistonFamily> PISTON_HEADS_TO_FAMILIES = Maps.newHashMap();

    public static PistonFamily BASIC;
    public static PistonFamily STRONG;
    public static PistonFamily FAST;
    public static PistonFamily STICKY;
    public static PistonFamily FRONT_POWERED;

    // TODO: Make all moving pistons dragon and wither immune

    public static PistonFamily.Builder register(BasicPistonHeadBlock headBlock) {
        PistonFamily.Builder builder = new PistonFamily.Builder(headBlock);
        PistonFamily blockFamily = PISTON_HEADS_TO_FAMILIES.put(headBlock, builder.getFamily());
        if (blockFamily != null) {
            throw new IllegalStateException(
                    "Duplicate piston family definition for " + Registry.BLOCK.getId(headBlock)
            );
        } else {
            return builder;
        }
    }

    public static Collection<PistonFamily> getFamilies() {
        return PISTON_HEADS_TO_FAMILIES.values();
    }

    public static PistonFamily getFamily(BasicPistonHeadBlock headBlock) {
        return PISTON_HEADS_TO_FAMILIES.get(headBlock);
    }
}
