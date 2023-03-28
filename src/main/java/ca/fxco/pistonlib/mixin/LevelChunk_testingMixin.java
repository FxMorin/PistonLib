package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.base.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(LevelChunk.class)
public class LevelChunk_testingMixin {

    @Redirect(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;" +
                            "onRemove(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/world/level/block/state/BlockState;Z)V"
            )
    )
    private void whyAmIBeingDeleted(BlockState state2, Level level, BlockPos blockPos, BlockState state, boolean bl) {
        if (state.isAir()) {
            if (state2.is(ModBlocks.LONG_STICKY_PISTON)) {
                System.out.println("LONG_STICKY_PISTON - pos: " + blockPos.toShortString());
                Arrays.asList(Thread.currentThread().getStackTrace()).forEach(System.out::println);
            } else if (state2.is(ModBlocks.LONG_MOVING_BLOCK)) {
                System.out.println("LONG_MOVING_BLOCK - pos: " + blockPos.toShortString());
                Arrays.asList(Thread.currentThread().getStackTrace()).forEach(System.out::println);
            }
        }
        state2.onRemove(level, blockPos, state, bl);
    }
}
