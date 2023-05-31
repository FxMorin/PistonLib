package ca.fxco.pistonlib.mixin.stickyGroup;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import net.minecraft.world.level.block.HoneyBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoneyBlock.class)
public class HoneyBlock_slimeMixin implements PLBlockBehaviour {

    @Override
    public StickyGroup pl$getStickyGroup() {
        return ModStickyGroups.HONEY;
    }
}
