package ca.fxco.pistonlib.blocks;

import java.util.Map;

import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StickySidesBlock extends Block {

    private final Map<Direction, StickyType> stickList;

    public StickySidesBlock(Properties properties, Map<Direction, StickyType> stickList) {
        super(properties);
        this.stickList = stickList;
    }

    @Override
    public boolean pl$usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public boolean pl$isSticky(BlockState state) {
        return true;
    }

    @Override
    public Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return stickList;
    }

    @Override
    public StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return stickList.getOrDefault(dir, StickyType.DEFAULT);
    }
}
