package ca.fxco.configurablepistons.mixin.stickyGroup;

import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.SlimeBlock;

@Mixin(SlimeBlock.class)
public class SlimeBlock_honeyMixin implements ConfigurablePistonStickiness {

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
