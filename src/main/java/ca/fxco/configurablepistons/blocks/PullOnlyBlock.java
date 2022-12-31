package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class PullOnlyBlock extends Block implements ConfigurablePistonBehavior {
    public PullOnlyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(BlockState state, Direction direction) {
        return false;
    }
}
