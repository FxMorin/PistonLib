package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.Registerer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockCollisionSpliterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisionSpliterator.class)
public class BlockCollisionSpliterator_movingPistonMixin {


    @Redirect(
            method = "computeNext()Lnet/minecraft/util/shape/VoxelShape;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    public boolean allMovingPistons(BlockState instance, Block block) {
        return instance.isIn(Registerer.MOVING_PISTONS);
    }
}
