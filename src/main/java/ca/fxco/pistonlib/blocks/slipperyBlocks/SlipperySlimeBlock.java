package ca.fxco.pistonlib.blocks.slipperyBlocks;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.api.pistonlib.block.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

public class SlipperySlimeBlock extends BaseSlipperyBlock implements ConfigurablePistonStickiness {

    public SlipperySlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return ModStickyGroups.SLIME;
    }
}
