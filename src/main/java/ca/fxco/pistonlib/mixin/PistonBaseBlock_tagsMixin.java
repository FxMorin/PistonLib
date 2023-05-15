package ca.fxco.pistonlib.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlock_tagsMixin {

    /*
     * In this mixin we basically change all the !state.isOf(PISTON) and regular piston to instead check against the
     * PISTONS TagKey, so that all pistons work the same with each other (Including vanilla)
     * 
     * We also replace any instances of PistonStructureResolver with the custom ConfigurablePistonStructureResolver.
     * This ensures even vanilla pistons will respect custom movability and sticky behavior.
     */


    @Shadow
    @Final
    private boolean isSticky;

    @Redirect(
            method = "checkIfExtend(Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/level/block/piston/PistonStructureResolver"
            )
    )
    private PistonStructureResolver customStructureResolver1(Level level, BlockPos pos,
                                                             Direction facing, boolean extend) {
        return newStructureResolver(level, pos, facing, extend);
    }

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(" +
                     "Lnet/minecraft/world/level/block/state/BlockState;" +
                     "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;" +
                     "Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
        )
    )
    private boolean modifyIsMovable(BlockState state, Level level, BlockPos pos,
                                    Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
        return ModBlocks.BASIC_PISTON.canMoveBlock(state, level, pos, moveDir, allowDestroy, pistonFacing);
    }

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 1
        )
    )
    public boolean allPistons(BlockState state, Block block) {
        return state.is(ModTags.PISTONS);
    }

    @Redirect(
        method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                 "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
            ordinal = 2
       )
    )
    public boolean skipIsPistonCheck(BlockState state, Block block) {
        return false;
    }

    @Redirect(
        method = "moveBlocks(Lnet/minecraft/world/level/Level;" +
                "Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Z",
        at = @At(
            value = "NEW",
            target = "net/minecraft/world/level/block/piston/PistonStructureResolver"
        )
    )
    private PistonStructureResolver customStructureResolver2(Level level, BlockPos pos,
                                                             Direction facing, boolean extend) {
        return newStructureResolver(level, pos, facing, extend);
    }

    private PistonStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        // the basic pistons should act exactly as vanilla pistons anyway
        return (this.isSticky ? ModBlocks.BASIC_STICKY_PISTON : ModBlocks.BASIC_PISTON)
           .newStructureResolver(level, pos, facing, extend ? 0 : 1, extend);
    }
}
