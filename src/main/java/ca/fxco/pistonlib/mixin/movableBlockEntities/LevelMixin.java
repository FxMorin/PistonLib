package ca.fxco.pistonlib.mixin.movableBlockEntities;

import java.util.Stack;

import ca.fxco.api.pistonlib.level.PLLevel;
import ca.fxco.pistonlib.mixin.accessors.BlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin implements PLLevel {

    private final Stack<BlockEntity> pl$queuedBlockEntities = new Stack<>();

    private BlockEntity pl$nextBlockEntity;

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void pl$pushBlockEntity(CallbackInfoReturnable<Boolean> cir) {
        this.pl$queuedBlockEntities.push(this.pl$nextBlockEntity);
        this.pl$nextBlockEntity = null;
    }

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void pl$popBlockEntity(CallbackInfoReturnable<Boolean> cir) {
        this.pl$queuedBlockEntities.pop();
    }

    @Override
    public void pl$prepareBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity) {
        this.pl$nextBlockEntity = blockEntity;
    }

    @Override
    public BlockEntity pl$getBlockEntityForPlacement(BlockPos pos, BlockState state) {
        BlockEntity blockEntity = this.pl$queuedBlockEntities.peek();

        if (blockEntity != null) {
            blockEntity.setLevel((Level)(Object)this);
            ((BlockEntityAccessor)blockEntity).setPos(pos);
            blockEntity.setBlockState(state);
        }

        return blockEntity;
    }
}
