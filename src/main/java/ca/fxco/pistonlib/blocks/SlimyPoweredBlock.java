package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

import net.minecraft.world.level.block.PoweredBlock;

public class SlimyPoweredBlock extends PoweredBlock {

	public SlimyPoweredBlock(Properties properties) {
        super(properties);
    }

    @Override
    public StickyGroup pl$getStickyGroup() {
        return ModStickyGroups.SLIME;
    }
}
