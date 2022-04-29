package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.helpers.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.helpers.StickyType;
import ca.fxco.configurablepistons.internal.AbstractBlockStateDirectionalSticky;
import ca.fxco.configurablepistons.internal.AbstractBlockStatePistonBehavior;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_pistonBehaviorMixin implements AbstractBlockStatePistonBehavior, AbstractBlockStateDirectionalSticky {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asBlockState();

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return ((ConfigurablePistonBehavior)this.getBlock()).usesConfigurablePistonBehavior();
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

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return ((ConfigurablePistonStickiness)this.getBlock()).usesConfigurablePistonStickiness();
    }

    @Override
    public boolean isSticky() {
        return ((ConfigurablePistonStickiness)this.getBlock()).isSticky(this.asBlockState());
    }

    @Override
    public Map<Direction, StickyType> stickySides() {
        return ((ConfigurablePistonStickiness)this.getBlock()).stickySides(this.asBlockState());
    }

    @Override
    public StickyType sideStickiness(Direction direction) {
        return ((ConfigurablePistonStickiness)this.getBlock()).sideStickiness(this.asBlockState(), direction);
    }
}
