package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.block.state.PLBlockStateBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateBase.class)
public class BlockStateBase_coreMixin implements PLBlockStateBase {

    @Shadow
    private Block getBlock() { return null; }

    @Shadow
    private BlockState asState() { return null; }

    @Override
    public Block pl$getBlock() {
        return this.getBlock();
    }

    @Override
    public BlockState pl$asState() {
        return this.asState();
    }
}
