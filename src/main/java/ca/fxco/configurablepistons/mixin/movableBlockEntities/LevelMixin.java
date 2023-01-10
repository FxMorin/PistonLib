package ca.fxco.configurablepistons.mixin.movableBlockEntities;

import java.util.Stack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ca.fxco.configurablepistons.interfaces.ILevel;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public abstract class LevelMixin implements ILevel {

    private final Stack<BlockEntity> queuedBlockEntities = new Stack<>();

    private BlockEntity nextBlockEntity;

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void rtPushBlockEntity(CallbackInfoReturnable<Boolean> cir) {
        queuedBlockEntities.push(nextBlockEntity);
        nextBlockEntity = null;
    }

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            shift = Shift.AFTER,
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private void rtPopBlockEntity(CallbackInfoReturnable<Boolean> cir) {
        queuedBlockEntities.pop();
    }

    @Override
    public void prepareBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity) {
        nextBlockEntity = blockEntity;
    }

    @Override
    public BlockEntity getBlockEntityForPlacement(BlockPos pos, BlockState state) {
        BlockEntity blockEntity = queuedBlockEntities.peek();

        if (blockEntity != null) {
            blockEntity.setLevel((Level)(Object)this);
            ((BlockEntityAccessor)blockEntity).setPos(pos);
            blockEntity.setBlockState(state);
        }

        return blockEntity;
    }
}
