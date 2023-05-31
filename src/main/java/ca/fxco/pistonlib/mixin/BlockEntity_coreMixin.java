package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.blockEntity.PLBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntity_coreMixin implements PLBlockEntity {
}
