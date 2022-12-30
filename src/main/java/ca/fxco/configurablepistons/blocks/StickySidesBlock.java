package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class StickySidesBlock extends Block implements ConfigurablePistonStickiness {

    private final Map<Direction, StickyType> stickList;

    public StickySidesBlock(Settings settings, Map<Direction, StickyType> stickList) {
        super(settings);
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
    public StickyType sideStickiness(BlockState state, Direction direction) {
        StickyType stickyType = stickList.get(direction);
        return stickyType == null ? StickyType.DEFAULT : stickyType;
    }
}
