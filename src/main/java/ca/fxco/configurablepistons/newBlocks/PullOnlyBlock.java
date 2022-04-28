package ca.fxco.configurablepistons.newBlocks;

import ca.fxco.configurablepistons.helpers.ConfigurablePistonBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class PullOnlyBlock extends Block implements ConfigurablePistonBehavior {
    public PullOnlyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(BlockState state) {
        return false;
    }
}
