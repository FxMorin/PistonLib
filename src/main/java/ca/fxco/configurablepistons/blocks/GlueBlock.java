package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class GlueBlock extends Block implements ConfigurablePistonStickiness {

    private static final Map<Direction, StickyType> GLUE_SIDES_LIST = new HashMap<>() {{
        put(Direction.UP, StickyType.STRONG);
        put(Direction.DOWN, StickyType.STRONG);
        put(Direction.NORTH, StickyType.STRONG);
        put(Direction.SOUTH, StickyType.STRONG);
        put(Direction.EAST, StickyType.STRONG);
        put(Direction.WEST, StickyType.STRONG);
    }};

    public GlueBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return GLUE_SIDES_LIST;
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        return StickyType.STRONG;
    }
}