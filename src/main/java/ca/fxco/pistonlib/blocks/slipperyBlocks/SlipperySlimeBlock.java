package ca.fxco.pistonlib.blocks.slipperyBlocks;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

public class SlipperySlimeBlock extends BaseSlipperyBlock {

    public SlipperySlimeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public StickyGroup pl$getStickyGroup() {
        return ModStickyGroups.SLIME;
    }
}
