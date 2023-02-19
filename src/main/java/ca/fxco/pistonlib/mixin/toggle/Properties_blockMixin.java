package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.pistonlib.impl.toggle.Toggleable;
import ca.fxco.pistonlib.impl.toggle.ToggleableProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.BooleanSupplier;

@Mixin(BlockBehaviour.Properties.class)
public class Properties_blockMixin implements ToggleableProperties<BlockBehaviour.Properties>, Toggleable {

    @Unique
    BooleanSupplier isDisabled = () -> false;

    @Override
    public BlockBehaviour.Properties setDisabled(BooleanSupplier isDisabled) {
        this.isDisabled = isDisabled;
        return (BlockBehaviour.Properties)(Object)this;
    }

    @Override
    public BooleanSupplier getIsDisabled() {
        return this.isDisabled;
    }
}
