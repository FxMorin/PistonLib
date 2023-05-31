package ca.fxco.api.pistonlib.block;

import java.util.Map;
import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import ca.fxco.api.pistonlib.toggle.Toggleable;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface collects all custom behavior that is injected into {@code Block}s.
 * Similarly to {@linkplain net.minecraft.world.level.block.state.BlockBehaviour BlockBehaviour},
 * it provides default implementations for all custom behavior.
 * 
 * Through the magic of Loom's interface injection feature you will not need to manually
 * implement this interface in your custom blocks. Simply extending
 * {@linkplain net.minecraft.world.level.block.Block Block} will allow you to override
 * and use all these methods.
 * 
 * Similar to how the methods in {@linkplain net.minecraft.world.level.block.state.BlockBehaviour BlockBehaviour}
 * are deprecated to encourage the use of the methods defined in
 * {@linkplain net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase BlockStateBase},
 * many methods are deprecated to encourage the use of the methods defined in
 * {@linkplain ca.fxco.api.pistonlib.block.state.PLBlockStateBase PLBlockStateBase}.
 */
public interface PLBlockBehaviour extends BlockPistonBehavior, BlockPistonMerging, BlockPistonStickiness, BlockQuasiPower, Toggleable {


    // BlockPistonBehavior

    @Deprecated
    @Override
    default int pl$getWeight(BlockState state) {
        return 1;
    }

    @Deprecated
    @Override
    default boolean pl$usesConfigurablePistonBehavior() {
        return false;
    }

    @Deprecated
    @Override
    default boolean pl$isMovable(Level level, BlockPos pos, BlockState state) {
        return true;
    }

    @Deprecated
    @Override
    default boolean pl$canPistonPush(Level level, BlockPos pos, BlockState state, Direction dir) {
        return true;
    }

    @Deprecated
    @Override
    default boolean pl$canPistonPull(Level level, BlockPos pos, BlockState state, Direction dir) {
        return true;
    }

    @Deprecated
    @Override
    default boolean pl$canBypassFused(BlockState state) {
        return false;
    }

    @Deprecated
    @Override
    default boolean pl$canDestroy(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    @Deprecated
    @Override
    default void pl$onPushEntityInto(Level level, BlockPos pos, BlockState state, Entity entity) {
    }


    // BlockPistonMerging

    @Deprecated
    @Override
    default boolean pl$usesConfigurablePistonMerging() {
        return false;
    }

    @Deprecated
    @Override
    default boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    @Deprecated
    @Override
    default boolean pl$canMergeFromSide(BlockState state, BlockGetter level, BlockPos pos, Direction pushDir) {
        return true;
    }

    @Deprecated
    @Override
    default BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }

    @Deprecated
    @Override
    default boolean pl$canMultiMerge() {
        return false;
    }

    @Deprecated
    @Override
    default boolean pl$canMultiMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return false;
    }

    @Deprecated
    @Override
    default BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }

    @Deprecated
    @Override
    default boolean pl$canUnMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        return false;
    }

    @Deprecated
    @Override
    default @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return null;
    }

    @Deprecated
    @Override
    default MergeRule pl$getBlockEntityMergeRules() {
        return MergeRule.NEVER;
    }


    // BlockPistonStickiness

    @Deprecated
    @Override
    default @Nullable StickyGroup pl$getStickyGroup() {
        return null;
    }

    @Deprecated
    @Override
    default boolean pl$hasStickyGroup() {
        return this.pl$getStickyGroup() != null;
    }

    @Deprecated
    @Override
    default boolean pl$usesConfigurablePistonStickiness() {
        return false;
    }

    @Deprecated
    @Override
    default boolean pl$isSticky(BlockState state) {
        return true;
    }

    @Deprecated
    @Override
    default Map<Direction, StickyType> pl$stickySides(BlockState state) {
        return Map.of();
    }

    @Deprecated
    @Override
    default StickyType pl$sideStickiness(BlockState state, Direction dir) {
        return StickyType.DEFAULT;
    }

    @Deprecated
    @Override
    default boolean pl$matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        return true;
    }


    // BlockQuasiPower

    @Deprecated
    @Override
    default int pl$getQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return state.getSignal(level, pos, dir);
    }

    @Deprecated
    @Override
    default int pl$getDirectQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return state.getDirectSignal(level, pos, dir);
    }

    @Deprecated
    @Override
    default boolean pl$isQuasiConductor(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isRedstoneConductor(level, pos);
    }


    // Toggleable

    @Override
    default BooleanSupplier pl$getIsDisabled() {
        return null;
    }
}
