package ca.fxco.pistonlib.blocks;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StickyChainBlock extends ChainBlock implements ConfigurablePistonStickiness {

    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesX_strong = Util.make(new HashMap<>(), map -> {
        map.put(Direction.EAST, StickyType.STRONG);
        map.put(Direction.WEST, StickyType.STRONG);
    });
    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesX_sticky = Util.make(new HashMap<>(), map -> {
        map.put(Direction.EAST, StickyType.STICKY);
        map.put(Direction.WEST, StickyType.STICKY);
    });
    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesY_strong = Util.make(new HashMap<>(), map -> {
        map.put(Direction.UP, StickyType.STRONG);
        map.put(Direction.DOWN, StickyType.STRONG);
    });
    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesY_sticky = Util.make(new HashMap<>(), map -> {
        map.put(Direction.UP, StickyType.STICKY);
        map.put(Direction.DOWN, StickyType.STICKY);
    });
    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesZ_strong = Util.make(new HashMap<>(), map -> {
        map.put(Direction.NORTH, StickyType.STRONG);
        map.put(Direction.SOUTH, StickyType.STRONG);
    });
    @Getter(lazy = true)
    private static final Map<Direction, StickyType> chainSidesZ_sticky = Util.make(new HashMap<>(), map -> {
        map.put(Direction.NORTH, StickyType.STICKY);
        map.put(Direction.SOUTH, StickyType.STICKY);
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
        if (PistonLibConfig.strongStickyChains) {
            return switch (state.getValue(AXIS)) {
                case X -> StickyChainBlock.getChainSidesX_strong();
                case Y -> StickyChainBlock.getChainSidesY_strong();
                case Z -> StickyChainBlock.getChainSidesZ_strong();
            };
        }
        return switch (state.getValue(AXIS)) {
            case X -> StickyChainBlock.getChainSidesX_sticky();
            case Y -> StickyChainBlock.getChainSidesY_sticky();
            case Z -> StickyChainBlock.getChainSidesZ_sticky();
        };
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS) ?
                (PistonLibConfig.strongStickyChains ? StickyType.STRONG : StickyType.STICKY) :
                StickyType.NO_STICK;
    }
}
