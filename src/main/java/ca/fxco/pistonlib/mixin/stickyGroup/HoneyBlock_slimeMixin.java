package ca.fxco.pistonlib.mixin.stickyGroup;

import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.HoneyBlock;

@Mixin(HoneyBlock.class)
public class HoneyBlock_slimeMixin implements ConfigurablePistonStickiness {

    @Override
    public StickyGroup getStickyGroup() {
        return StickyGroup.HONEY;
    }
}
