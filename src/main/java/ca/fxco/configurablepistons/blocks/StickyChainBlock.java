package ca.fxco.configurablepistons.blocks;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StickyChainBlock extends ChainBlock implements ConfigurablePistonStickiness {

    private static final Map<Direction, StickyType> CHAIN_SIDES_LIST = Util.make(new HashMap<>(), map -> {
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
        return CHAIN_SIDES_LIST;
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return StickyType.STICKY;
    }
}
