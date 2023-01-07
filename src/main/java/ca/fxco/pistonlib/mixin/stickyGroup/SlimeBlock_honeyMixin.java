package ca.fxco.pistonlib.mixin.stickyGroup;

import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.SlimeBlock;

@Mixin(SlimeBlock.class)
public class SlimeBlock_honeyMixin implements ConfigurablePistonStickiness {

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.SLIME;
    }
}
