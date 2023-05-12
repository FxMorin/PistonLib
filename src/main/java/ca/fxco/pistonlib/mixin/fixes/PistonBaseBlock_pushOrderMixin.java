package ca.fxco.pistonlib.mixin.fixes;

import ca.fxco.pistonlib.PistonLibConfig;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;

/**
 * Prevents pistons from being able to break blocks with a hardness value of -1.0F
 * You need headless pistons to be able to break these blocks using pistons.
 */

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlock_pushOrderMixin {

    @Redirect(
            method = "moveBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;"
            )
    )
    private HashMap<BlockPos, BlockState> fixLocationalHashmap() {
        return PistonLibConfig.locationalUpdateOrderFix ? Maps.newLinkedHashMap() : Maps.newHashMap();
    }
}
