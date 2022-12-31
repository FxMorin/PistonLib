package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonHandler.class)
public abstract class PistonHandler_isMovableMixin {

    @Shadow
    @Final
    private boolean retracted;

    @Shadow
    protected abstract boolean tryMove(BlockPos pos, Direction dir);


    @Redirect(
            method = "calculatePush()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean useCustomIsMovable1(BlockState state, World world, BlockPos pos,
                                        Direction dir, boolean canBreak, Direction pistonDir) {
        return PistonUtils.isMovable(state, world, pos, dir, canBreak, pistonDir);
    }


    @Redirect(
            method = "tryMove(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean useCustomIsMovable2(BlockState state, World world, BlockPos pos,
                                        Direction dir, boolean canBreak, Direction pistonDir) {
        return PistonUtils.isMovable(state, world, pos, dir, canBreak, pistonDir);
    }


    @Redirect(
            method = "calculatePush()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/piston/PistonHandler;" +
                            "tryMove(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean useCustomIsMovable1(PistonHandler instance, BlockPos pos, Direction dir) {
        return this.tryMove(pos,this.retracted ? dir : dir.getOpposite());
    }


    @Redirect(
            method = "isBlockSticky(Lnet/minecraft/block/BlockState;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 0
            )
    )
    private static boolean isBlockSticky(BlockState state, Block block) {
        return ((ConfigurablePistonStickiness)state.getBlock()).hasStickyGroup();
    }


    @Redirect(
            method = "isBlockSticky(Lnet/minecraft/block/BlockState;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 1
            )
    )
    private static boolean ignoreSecondCheck(BlockState state, Block block) {
        return false;
    }
}
