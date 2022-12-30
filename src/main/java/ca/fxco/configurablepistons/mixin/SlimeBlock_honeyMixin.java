package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.SlimeBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlimeBlock.class)
public class SlimeBlock_honeyMixin implements ConfigurablePistonStickiness {

    @Override
    public boolean canStick(BlockState state, Block adjBlock) {
        return !(adjBlock instanceof HoneyBlock);
    }
}
