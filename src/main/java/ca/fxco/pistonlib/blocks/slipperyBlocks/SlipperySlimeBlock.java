package ca.fxco.pistonlib.blocks.slipperyBlocks;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

public class SlipperySlimeBlock extends BaseSlipperyBlock implements ConfigurablePistonStickiness {

    public SlipperySlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
