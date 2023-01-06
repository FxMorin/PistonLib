package ca.fxco.configurablepistons.mixin.movingPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.configurablepistons.base.ModTags;

import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Shulker.class)
public class Shulker_movingPistonMixin {

    @Redirect(
        method = "isPositionBlocked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    public boolean allMovingPistons(BlockState state, Block block) {
        return state.is(ModTags.MOVING_PISTONS);
    }
}
