package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

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
