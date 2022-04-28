package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.ModTags;
import ca.fxco.configurablepistons.helpers.PistonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBlock_tagsMixin {

    /*
     * In this mixin we basically change all the !state.isOf(PISTON) and regular piston to instead check against the
     * PISTONS TagKey, so that all pistons work the same with each other (Including vanilla)
     */


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;" +
                            "Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"
            )
    )
    private boolean modifyIsMovable(BlockState state, World world, BlockPos pos,
                                    Direction direction, boolean canBreak, Direction pistonDir) {
        return PistonUtils.isMovable(state, world, pos, direction, canBreak, pistonDir);
    }


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 1
            )
    )
    public boolean ifItsAPiston(BlockState state, Block block) {
        return state.isIn(ModTags.PISTONS);
    }


    @Redirect(
            method = "onSyncedBlockEvent(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 2
            )
    )
    public boolean skipThis(BlockState state, Block block) {
        return false;
    }
}
