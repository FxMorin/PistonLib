package ca.fxco.pistonlib.mixin.movingPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.pistonlib.base.ModTags;

import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockCollisions.class)
public class BlockCollisions_movingPistonMixin {

    @Redirect(
        method = "computeNext()Lnet/minecraft/world/phys/shapes/VoxelShape;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    public boolean allMovingPistons(BlockState state, Block block) {
        return state.is(ModTags.MOVING_PISTONS);
    }
}
