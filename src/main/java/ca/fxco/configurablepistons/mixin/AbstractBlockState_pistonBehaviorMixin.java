package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.pistonLogic.internal.AbstractBlockStateExpandedSticky;
import ca.fxco.configurablepistons.pistonLogic.internal.AbstractBlockStatePistonBehavior;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockState_pistonBehaviorMixin
        implements AbstractBlockStatePistonBehavior, AbstractBlockStateExpandedSticky {

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
    public boolean canPistonPush(Direction direction) {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPush(this.asBlockState(), direction);
    }

    @Override
    public boolean canPistonPull(Direction direction) {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPull(this.asBlockState(), direction);
    }

    @Override
    public boolean canDestroy() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canDestroy(this.asBlockState());
    }

    @Override
    public @Nullable StickyGroup getStickyGroup() {
        return ((ConfigurablePistonStickiness)this.getBlock()).getStickyGroup();
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
