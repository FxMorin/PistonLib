package ca.fxco.configurablepistons.mixin;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.configurablepistons.pistonLogic.internal.BlockStateBaseExpandedSticky;
import ca.fxco.configurablepistons.pistonLogic.internal.BlockStateBasePushReaction;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateBase.class)
public class BlockStateBase_pistonBehaviorMixin implements BlockStateBasePushReaction, BlockStateBaseExpandedSticky {

    @Shadow
    private Block getBlock() { return null; }

    @Shadow
    private BlockState asState() { return null; }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return ((ConfigurablePistonBehavior)this.getBlock()).usesConfigurablePistonBehavior();
    }

    @Override
    public boolean isMovable() {
        return ((ConfigurablePistonBehavior)this.getBlock()).isMovable(this.asState());
    }

    @Override
    public boolean canPistonPush(Direction direction) {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPush(this.asState(), direction);
    }

    @Override
    public boolean canPistonPull(Direction direction) {
        return ((ConfigurablePistonBehavior)this.getBlock()).canPistonPull(this.asState(), direction);
    }

    @Override
    public boolean canBypassFused() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canBypassFused(this.asState());
    }

    @Override
    public boolean canDestroy() {
        return ((ConfigurablePistonBehavior)this.getBlock()).canDestroy(this.asState());
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
        return ((ConfigurablePistonStickiness)this.getBlock()).isSticky(this.asState());
    }

    @Override
    public Map<Direction, StickyType> stickySides() {
        return ((ConfigurablePistonStickiness)this.getBlock()).stickySides(this.asState());
    }

    @Override
    public StickyType sideStickiness(Direction direction) {
        return ((ConfigurablePistonStickiness)this.getBlock()).sideStickiness(this.asState(), direction);
    }
}
