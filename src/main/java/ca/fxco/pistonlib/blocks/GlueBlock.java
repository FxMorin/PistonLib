package ca.fxco.pistonlib.blocks;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.api.pistonlib.block.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GlueBlock extends Block implements ConfigurablePistonStickiness {

    private static final Map<Direction, StickyType> GLUE_SIDES_LIST = Util.make(new HashMap<>(), map -> {
        map.put(Direction.UP, StickyType.STRONG);
        map.put(Direction.DOWN, StickyType.STRONG);
        map.put(Direction.NORTH, StickyType.STRONG);
        map.put(Direction.SOUTH, StickyType.STRONG);
        map.put(Direction.EAST, StickyType.STRONG);
        map.put(Direction.WEST, StickyType.STRONG);
    });

    public GlueBlock(Properties properties) {
        super(properties);
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