package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.ShulkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerEntity.class)
public class ShulkerEntity_movingPistonMixin {


    @Redirect(
            method = "isInvalidPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    public boolean allMovingPistons(BlockState instance, Block block) {
        return instance.isIn(ModTags.MOVING_PISTONS);
    }
}
