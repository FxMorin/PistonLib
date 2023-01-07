package ca.fxco.pistonlib.mixin.movingPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.pistonlib.base.ModTags;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntity_movingPistonMixin {

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    public boolean allMovingPistons(BlockState instance, Block block) {
        return instance.is(ModTags.MOVING_PISTONS);
    }
}
