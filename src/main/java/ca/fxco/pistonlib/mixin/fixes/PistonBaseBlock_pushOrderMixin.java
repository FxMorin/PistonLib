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
 * Fixes the piston update order being locational
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
