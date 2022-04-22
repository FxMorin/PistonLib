package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonExtensionBlock.class)
public class PistonExtensionBlock_blockEntityMixin {


    @Redirect(
            method = "createBlockEntityPiston",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/block/entity/PistonBlockEntity"
            )
    )
    private static PistonBlockEntity createBlockEntityPiston(BlockPos pos, BlockState state, BlockState pushedBlock,
                                                             Direction facing, boolean extending, boolean source) {
        return new BasicPistonBlockEntity(pos, state, pushedBlock, facing, extending, source);
    }
}
