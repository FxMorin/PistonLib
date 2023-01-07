package ca.fxco.configurablepistons.blocks.slipperyBlocks;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

public class SlipperySlimeBlock extends BaseSlipperyBlock implements ConfigurablePistonStickiness {

    public SlipperySlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
