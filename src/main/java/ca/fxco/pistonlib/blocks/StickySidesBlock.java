package ca.fxco.pistonlib.blocks;

import java.util.Map;

import ca.fxco.api.pistonlib.block.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StickySidesBlock extends Block implements ConfigurablePistonStickiness {

    private final Map<Direction, StickyType> stickList;

    public StickySidesBlock(Properties properties, Map<Direction, StickyType> stickList) {
        super(properties);
        this.stickList = stickList;
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public boolean isSticky(BlockState state) {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return stickList;
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return stickList.getOrDefault(dir, StickyType.DEFAULT);
    }
}
