package ca.fxco.configurablepistons.families;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.stream.Stream;

public class PistonFamilies {
    private static final Map<Block, PistonFamily> PISTON_HEADS_TO_FAMILIES = Maps.newHashMap();

    public static final PistonFamily BASIC;
    public static final PistonFamily STRONG;
    public static final PistonFamily FAST;

    // TODO: Make all moving pistons dragon and wither immune

    public static PistonFamily.Builder register(BasicPistonHeadBlock headBlock) {
        PistonFamily.Builder builder = new PistonFamily.Builder(headBlock);
        PistonFamily blockFamily = PISTON_HEADS_TO_FAMILIES.put(headBlock, builder.build());
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate piston family definition for " + Registry.BLOCK.getId(headBlock));
        } else {
            return builder;
        }
    }

    public static Stream<PistonFamily> getFamilies() {
        return PISTON_HEADS_TO_FAMILIES.values().stream();
    }

    public static PistonFamily getFamily(BasicPistonHeadBlock headBlock) {
        return PISTON_HEADS_TO_FAMILIES.get(headBlock);
    }

    static {
        BASIC = register(ConfigurablePistons.BASIC_PISTON_HEAD).piston(ConfigurablePistons.BASIC_PISTON).sticky(ConfigurablePistons.BASIC_STICKY_PISTON).extension(ConfigurablePistons.BASIC_MOVING_PISTON).build();
        STRONG = register(ConfigurablePistons.STRONG_PISTON_HEAD).piston(ConfigurablePistons.STRONG_PISTON).sticky(ConfigurablePistons.STRONG_STICKY_PISTON).extension(ConfigurablePistons.STRONG_MOVING_PISTON).build();
        FAST = register(ConfigurablePistons.FAST_PISTON_HEAD).piston(ConfigurablePistons.FAST_PISTON).sticky(ConfigurablePistons.FAST_STICKY_PISTON).extension(ConfigurablePistons.FAST_MOVING_PISTON).build();
    }
}
