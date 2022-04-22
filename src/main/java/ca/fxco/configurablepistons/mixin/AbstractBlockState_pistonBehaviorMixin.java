package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.helpers.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.internal.AbstractBlockStatePistonBehavior;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_pistonBehaviorMixin implements AbstractBlockStatePistonBehavior {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return ((ConfigurablePistonBehavior)this.getBlock()).usesConfigurablePistonBehavior(this.asBlockState());
    }

    @Override
    public boolean isMovable() {
        return ((ConfigurablePistonBehavior)this.getBlock()).isMovable(this.asBlockState());
    }

    @Override
    public boolean canPistonPush() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPush(this.asBlockState());
    }

    @Override
    public boolean canPistonPull() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPull(this.asBlockState());
    }

    @Override
    public boolean canDestroy() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canDestroy(this.asBlockState());
    }
}
