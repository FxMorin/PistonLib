package ca.fxco.pistonlib.mixin.movingPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.pistonlib.base.ModTags;

import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(TerrainParticle.Provider.class)
public class Factory_movingPistonMixin {

    @Redirect(
        method = "createParticle(Lnet/minecraft/core/particles/BlockParticleOption;" +
                "Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    private boolean allMovingPistons(BlockState state, Block block) {
        return state.is(ModTags.MOVING_PISTONS);
    }
}
