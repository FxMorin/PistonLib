package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.RedstoneBlock;

public class SlimyRedstoneBlock extends RedstoneBlock implements ConfigurablePistonStickiness {
    public SlimyRedstoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
