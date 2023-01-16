package ca.fxco.pistonlib.blocks.slipperyBlocks;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroups;

public class SlipperySlimeBlock extends BaseSlipperyBlock implements ConfigurablePistonStickiness {

    public SlipperySlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroups.SLIME;
    }
}
