package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.impl.BlockPowerRedirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

    @Inject(
            method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/core/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isSignalSource()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void shouldConnectTo(BlockState blockState, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.getBlock() instanceof BlockPowerRedirection bpr) {
            cir.setReturnValue(bpr.canRedirectRedstone(blockState, dir));
        }
    }
}
