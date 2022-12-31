package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.SlimeBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlimeBlock.class)
public class SlimeBlock_honeyMixin implements ConfigurablePistonStickiness {

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
