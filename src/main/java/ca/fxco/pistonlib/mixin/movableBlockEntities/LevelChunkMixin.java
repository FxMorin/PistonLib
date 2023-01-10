package ca.fxco.pistonlib.mixin.movableBlockEntities;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ca.fxco.pistonlib.interfaces.ILevel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Shadow @Final private Level level;

    @Inject(
        method = "setBlockState",
        at = @At(
            value = "FIELD",
            ordinal = 1,
            target = "Lnet/minecraft/world/level/Level;isClientSide:Z"
        )
    )
    private void rtPlaceMovedBlockEntity(BlockPos pos, BlockState state, boolean movedByPiston, CallbackInfoReturnable<BlockState> cir) {
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = ((ILevel)level).getBlockEntityForPlacement(pos, state);

            if (blockEntity != null) {
                ((LevelChunk)(Object)this).addAndRegisterBlockEntity(blockEntity);
            }
        }
    }
}
