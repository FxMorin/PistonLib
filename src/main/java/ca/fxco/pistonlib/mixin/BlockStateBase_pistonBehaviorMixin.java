package ca.fxco.pistonlib.mixin;

import java.util.Map;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import ca.fxco.pistonlib.pistonLogic.internal.BlockStateBaseMerging;
import ca.fxco.pistonlib.impl.BlockQuasiPower;
import ca.fxco.pistonlib.impl.BlockStateQuasiPower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import com.mojang.datafixers.util.Pair;
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
public abstract class BlockStateBase_pistonBehaviorMixin implements BlockStateBasePushReaction, BlockStateBaseExpandedSticky, BlockStateBaseMerging, BlockStateQuasiPower {

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
    public int getDirectQuasiSignal(BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return ((BlockQuasiPower)this.getBlock()).getDirectQuasiSignal(this.asState(), blockGetter, pos, dir, dist);
    }

    @Override
    public boolean isQuasiConductor(BlockGetter blockGetter, BlockPos blockPos) {
        return ((BlockQuasiPower)this.getBlock()).isQuasiConductor(this.asState(), blockGetter, blockPos);
    }

    @Override
    public boolean usesConfigurablePistonMerging() {
        return ((ConfigurablePistonMerging)this.getBlock()).usesConfigurablePistonMerging();
    }

    @Override
    public boolean canMerge(BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction direction) {
        return ((ConfigurablePistonMerging)this.getBlock()).canMerge(this.asState(), blockGetter, blockPos, mergingIntoState, direction);
    }

    @Override
    public boolean canMergeFromSide(BlockGetter blockGetter, BlockPos blockPos, Direction pushDirection) {
        return ((ConfigurablePistonMerging)this.getBlock()).canMergeFromSide(this.asState(), blockGetter, blockPos, pushDirection);
    }

    @Override
    public BlockState doMerge(BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction direction) {
        return ((ConfigurablePistonMerging)this.getBlock()).doMerge(this.asState(), blockGetter, blockPos, mergingIntoState, direction);
    }

    @Override
    public boolean canMultiMerge() {
        return ((ConfigurablePistonMerging)this.getBlock()).canMultiMerge();
    }

    @Override
    public boolean canMultiMerge(BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction direction, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return ((ConfigurablePistonMerging)this.getBlock()).canMultiMerge(this.asState(), blockGetter, blockPos, mergingIntoState, direction, currentlyMerging);
    }

    @Override
    public BlockState doMultiMerge(BlockGetter blockGetter, BlockPos blockPos, Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return ((ConfigurablePistonMerging)this.getBlock()).doMultiMerge(blockGetter, blockPos, states, mergingIntoState);
    }

    @Override
    public boolean canUnMerge(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return ((ConfigurablePistonMerging)this.getBlock()).canUnMerge(this.asState(), blockGetter, blockPos, direction);
    }

    @Override
    public @Nullable Pair<BlockState, BlockState> doUnMerge(BlockGetter blockGetter, BlockPos blockPos, BlockState pistonBlockState, Direction direction) {
        return ((ConfigurablePistonMerging)this.getBlock()).doUnMerge(this.asState(), blockGetter, blockPos, pistonBlockState, direction);
    }
}
