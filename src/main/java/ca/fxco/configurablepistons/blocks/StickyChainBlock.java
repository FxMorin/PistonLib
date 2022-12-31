package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class StickyChainBlock extends ChainBlock implements ConfigurablePistonStickiness {

    private static final Map<Direction, StickyType> CHAIN_SIDES_LIST = new HashMap<>() {{
        put(Direction.NORTH, StickyType.STICKY);
        put(Direction.SOUTH, StickyType.STICKY);
    }};

    public StickyChainBlock(Settings settings) {
        super(settings);
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
    public StickyType sideStickiness(BlockState state, Direction direction) {
        return StickyType.STICKY;
    }
}
