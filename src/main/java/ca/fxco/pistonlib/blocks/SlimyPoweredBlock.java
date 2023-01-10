package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.PoweredBlock;

public class SlimyPoweredBlock extends PoweredBlock implements ConfigurablePistonStickiness {

	public SlimyPoweredBlock(Properties properties) {
        super(properties);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
