package ca.fxco.pistonlib.mixin.quasi;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
public class DispenserBlock_quasiMixin {

    @Redirect(
            method = "neighborChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z",
                    ordinal = 1
            )
    )
    private boolean useQuasiSignalCheck(Level level, BlockPos pos) {
        return level.pl$hasQuasiNeighborSignal(pos.below(), 1);
    }
}
