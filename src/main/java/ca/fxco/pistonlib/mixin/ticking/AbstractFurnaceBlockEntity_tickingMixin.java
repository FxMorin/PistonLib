package ca.fxco.pistonlib.mixin.ticking;

import ca.fxco.api.pistonlib.block.MovingTickable;
import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.helpers.FakeBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.serverTick;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntity_tickingMixin implements MovingTickable {

    @Override
    public void pl$movingTick(Level level, BlockState state, BlockPos toPos, Direction dir, float progress, float speed, boolean merging) {
        if (PistonLibConfig.cookWhileMoving) {
            serverTick(level, FakeBlockPos.of(toPos), state, (AbstractFurnaceBlockEntity)(Object)this);
        }
    }

    @Inject(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void skipRemainingWorldEvents(Level level, BlockPos blockPos, BlockState blockState,
                                                 AbstractFurnaceBlockEntity furnaceBlockEntity, CallbackInfo ci) {
        if (blockPos instanceof FakeBlockPos) {
            ci.cancel();
        }
    }
}
