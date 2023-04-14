package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.impl.BlockEntityPostLoad;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunk_blockEntityMixin {

    @WrapWithCondition(
            method = "addAndRegisterBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunk;" +
                            "updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
            )
    )
    private boolean conditionallyAddTickers(LevelChunk level, BlockEntity blockEntity) {
        if (blockEntity instanceof BasicMovingBlockEntity bmbe) {
            return bmbe.hasControl();
        }
        return true;
    }

    @Inject(
            method = "method_32920(Lnet/minecraft/world/level/block/entity/BlockEntity;)V",
            at = @At("RETURN")
    )
    private void blockEntityPostLoad(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof BlockEntityPostLoad bepl) {
            bepl.onPostChunkLoad();
        }
    }
}
