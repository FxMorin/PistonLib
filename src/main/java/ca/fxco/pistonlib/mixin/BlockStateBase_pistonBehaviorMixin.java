package ca.fxco.pistonlib.mixin;

import java.util.Map;

import ca.fxco.pistonlib.impl.BlockQuasiPower;
import ca.fxco.pistonlib.impl.BlockStateQuasiPower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ca.fxco.pistonlib.pistonLogic.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.StickyType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.internal.BlockStateBaseExpandedSticky;
import ca.fxco.pistonlib.pistonLogic.internal.BlockStateBasePushReaction;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateBase.class)
public abstract class BlockStateBase_pistonBehaviorMixin implements BlockStateBasePushReaction, BlockStateBaseExpandedSticky, BlockStateQuasiPower {

    @Shadow
    public Block getBlock() { return null; }

    @Shadow
    protected BlockState asState() { return null; }

    @Shadow public abstract boolean isRedstoneConductor(BlockGetter blockGetter, BlockPos blockPos);

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

    @Override
    public int getQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction dir, int dist) {
        return ((BlockQuasiPower)this.getBlock()).getQuasiSignal(this.asState(), blockGetter, blockPos, dir, dist);
    }

    @Override
    public boolean hasQuasiSignal(BlockGetter blockGetter, BlockPos blockPos, Direction dir, int dist) {
        return ((BlockQuasiPower)this.getBlock()).hasQuasiSignal(this.asState(), blockGetter, blockPos, dir, dist);
    }

    @Override
    public boolean isQuasiConductor(BlockGetter blockGetter, BlockPos blockPos) {
        return ((BlockQuasiPower)this.getBlock()).isQuasiConductor(this.asState(), blockGetter, blockPos);
    }
}
