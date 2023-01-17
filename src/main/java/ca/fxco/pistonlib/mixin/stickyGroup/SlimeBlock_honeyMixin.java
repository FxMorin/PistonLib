package ca.fxco.pistonlib.mixin.stickyGroup;

import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;

import net.minecraft.world.level.block.SlimeBlock;

@Mixin(SlimeBlock.class)
public class SlimeBlock_honeyMixin implements ConfigurablePistonStickiness {

    @Override
    public StickyGroup getStickyGroup() {
        return ModStickyGroups.SLIME;
    }
}
