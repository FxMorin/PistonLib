package ca.fxco.pistonlib.blocks;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StickyChainBlock extends ChainBlock implements ConfigurablePistonStickiness {

    private static final StickyType CHAIN_STICKY_TYPE = PistonLibConfig.strongStickyChains ? StickyType.STRONG : StickyType.STICKY;

    private static final Map<Direction, StickyType> CHAIN_SIDES_X = Util.make(new HashMap<>(), map -> {
        map.put(Direction.EAST, CHAIN_STICKY_TYPE);
        map.put(Direction.WEST, CHAIN_STICKY_TYPE);
    });
    private static final Map<Direction, StickyType> CHAIN_SIDES_Y = Util.make(new HashMap<>(), map -> {
        map.put(Direction.UP, CHAIN_STICKY_TYPE);
        map.put(Direction.DOWN, CHAIN_STICKY_TYPE);
    });
    private static final Map<Direction, StickyType> CHAIN_SIDES_Z = Util.make(new HashMap<>(), map -> {
        map.put(Direction.NORTH, CHAIN_STICKY_TYPE);
        map.put(Direction.SOUTH, CHAIN_STICKY_TYPE);
    });

    public StickyChainBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return switch(state.getValue(AXIS)) {
            case X -> CHAIN_SIDES_X;
            case Y -> CHAIN_SIDES_Y;
            case Z -> CHAIN_SIDES_Z;
        };
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS) ?
                CHAIN_STICKY_TYPE :
                StickyType.NO_STICK;
    }
}
