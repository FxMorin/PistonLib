package ca.fxco.pistonlib.blocks;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StickylessBlock extends Block {

    private static final Map<Direction, StickyType> STICKYLESS_LIST = Util.make(new HashMap<>(), map -> {
        map.put(Direction.UP, StickyType.NO_STICK);
        map.put(Direction.DOWN, StickyType.NO_STICK);
        map.put(Direction.NORTH, StickyType.NO_STICK);
        map.put(Direction.SOUTH, StickyType.NO_STICK);
        map.put(Direction.EAST, StickyType.NO_STICK);
        map.put(Direction.WEST, StickyType.NO_STICK);
    });

    public StickylessBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return true;
    }

    @Override // Might try setting this to false, and not using the list. Should work the same
    public boolean pl$isSticky(BlockState state) {
        return true;
    }

    @Override
    public Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return STICKYLESS_LIST;
    }

    @Override
    public StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return StickyType.NO_STICK;
    }
}
