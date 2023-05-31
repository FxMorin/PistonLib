package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.class)
public class BlockBehaviour_coreMixin implements PLBlockBehaviour {
}
