package ca.fxco.configurablepistons.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolver_isMovableMixin {

    @Shadow
    @Final
    private boolean extend;

    @Shadow
    private boolean addBlockLine(BlockPos pos, Direction dir) { return false; }

    @Redirect(
        method = "resolve()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;" +
                     "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;" +
                     "Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
        )
    )
    private boolean customIsMovable1(BlockState state, Level level, BlockPos pos, Direction moveDir,
                                     boolean allowDestroy, Direction pistonFacing) {
        return PistonUtils.isMovable(state, level, pos, moveDir, allowDestroy, pistonFacing);
    }

    @Redirect(
            method = "addBlockLine(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;" +
                            "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
            )
    )
    private boolean customIsMovable2(BlockState state, Level level, BlockPos pos, Direction moveDir,
                                     boolean allowDestroy, Direction pistonFacing) {
        return PistonUtils.isMovable(state, level, pos, moveDir, allowDestroy, pistonFacing);
    }

    @Redirect(
        method = "resolve()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;" +
                     "addBlockLine(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"
        )
    )
    private boolean customIsMovable(PistonStructureResolver structureResolver, BlockPos pos, Direction dir) {
        return this.addBlockLine(pos,this.extend ? dir : dir.getOpposite());
    }

    @Redirect(
        method = "isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 0
        )
    )
    private static boolean customIsBlockSticky(BlockState state, Block block) {
        return ((ConfigurablePistonStickiness)state.getBlock()).hasStickyGroup();
    }

    @Redirect(
        method = "isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 1
        )
    )
    private static boolean skipSecondIsStickyCheck(BlockState state, Block block) {
        return false;
    }
}
