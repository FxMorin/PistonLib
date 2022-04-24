package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.Registerer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.BlockDustParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockDustParticle.Factory.class)
public class BlockDustParticle$Factory_movingPistonMixin {


    @Redirect(
            method = "createParticle(Lnet/minecraft/particle/BlockStateParticleEffect;" +
                    "Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    private boolean isInMovingPistons(BlockState state, Block block) {
        return state.isIn(Registerer.MOVING_PISTONS);
    }
}
