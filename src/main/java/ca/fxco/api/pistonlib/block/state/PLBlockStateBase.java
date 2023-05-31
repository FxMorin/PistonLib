package ca.fxco.api.pistonlib.block.state;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import ca.fxco.api.pistonlib.block.BlockPistonMerging;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface collects all custom behavior that is injected into {@code BlockState}s.
 * Similarly to {@linkplain net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase BlockStateBase},
 * it simply forwards method calls to the block's implementations.
 */
@SuppressWarnings("deprecation")
public interface PLBlockStateBase extends BlockStatePistonBehavior, BlockStatePistonMerging, BlockStatePistonStickiness, BlockStateQuasiPower {

    /**
     * for internal use - this method is equivalent to {@linkplain net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getBlock BlockStateBase.getBlock}
     */
    default Block pl$getBlock() {
        throw new UnsupportedOperationException();
    }

    /**
     * for internal use - this method is equivalent to {@linkplain net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#asState BlockStateBase.asState}
     */
    default BlockState pl$asState() {
        throw new UnsupportedOperationException();
    }


    // BlockStatePistonBehavior

    @Override
    default int pl$getWeight() {
        return this.pl$getBlock().pl$getWeight(this.pl$asState());
    }

    @Override
    default boolean pl$usesConfigurablePistonBehavior() {
        return this.pl$getBlock().pl$usesConfigurablePistonBehavior();
    }

    @Override
    default boolean pl$isMovable(Level level, BlockPos pos) {
        return this.pl$getBlock().pl$isMovable(level, pos, this.pl$asState());
    }

    @Override
    default boolean pl$canPistonPush(Level level, BlockPos pos, Direction dir) {
        return this.pl$getBlock().pl$canPistonPush(level, pos, this.pl$asState(), dir);
    }

    @Override
    default boolean pl$canPistonPull(Level level, BlockPos pos, Direction dir) {
        return this.pl$getBlock().pl$canPistonPull(level, pos, this.pl$asState(), dir);
    }

    @Override
    default boolean pl$canBypassFused() {
        return this.pl$getBlock().pl$canBypassFused(this.pl$asState());
    }

    @Override
    default boolean pl$canDestroy(Level level, BlockPos pos) {
        return this.pl$getBlock().pl$canDestroy(level, pos, this.pl$asState());
    }

    @Override
    default void pl$onPushEntityInto(Level level, BlockPos pos, Entity entity) {
        this.pl$getBlock().pl$onPushEntityInto(level, pos, this.pl$asState(), entity);
    }


    // BlockStatePistonMerging

    @Override
    default boolean pl$usesConfigurablePistonMerging() {
        return this.pl$getBlock().pl$usesConfigurablePistonMerging();
    }

    @Override
    default boolean pl$canMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return this.pl$getBlock().pl$canMerge(this.pl$asState(), level, pos, mergingIntoState, dir);
    }

    @Override
    default boolean pl$canMergeFromSide(BlockGetter level, BlockPos pos, Direction pushDir) {
        return this.pl$getBlock().pl$canMergeFromSide(this.pl$asState(), level, pos, pushDir);
    }

    @Override
    default BlockState pl$doMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir) {
        return this.pl$getBlock().pl$doMerge(this.pl$asState(), level, pos, mergingIntoState, dir);
    }

    @Override
    default boolean pl$canMultiMerge() {
        return this.pl$getBlock().pl$canMultiMerge();
    }

    @Override
    default boolean pl$canMultiMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return this.pl$getBlock().pl$canMultiMerge(this.pl$asState(), level, pos, mergingIntoState, dir, currentlyMerging);
    }

    @Override
    default BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction,BlockState> states, BlockState mergingIntoState) {
        return this.pl$getBlock().pl$doMultiMerge(level, pos, states, mergingIntoState);
    }

    @Override
    default boolean pl$canUnMerge(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        return this.pl$getBlock().pl$canUnMerge(this.pl$asState(), level, pos, neighborState, dir);
    }

    @Override
    default @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockGetter level, BlockPos pos, Direction dir){
        return this.pl$getBlock().pl$doUnMerge(this.pl$asState(), level, pos, dir);
    }

    @Override
    default BlockPistonMerging.MergeRule pl$getBlockEntityMergeRules() {
        return this.pl$getBlock().pl$getBlockEntityMergeRules();
    }


    // BlockStatePistonStickiness

    @Override
    default @Nullable StickyGroup pl$getStickyGroup() {
        return this.pl$getBlock().pl$getStickyGroup();
    }

    @Override
    default boolean pl$hasStickyGroup() {
        return this.pl$getBlock().pl$hasStickyGroup();
    }

    @Override
    default boolean pl$usesConfigurablePistonStickiness() {
        return this.pl$getBlock().pl$usesConfigurablePistonStickiness();
    }

    @Override
    default boolean pl$isSticky() {
        return this.pl$getBlock().pl$isSticky(this.pl$asState());
    }

    @Override
    default Map<Direction, StickyType> pl$stickySides() {
        return this.pl$getBlock().pl$stickySides(this.pl$asState());
    }

    @Override
    default StickyType pl$sideStickiness(Direction dir) {
        return this.pl$getBlock().pl$sideStickiness(this.pl$asState(), dir);
    }

    @Override
    default boolean pl$matchesStickyConditions(BlockState neighborState, Direction dir) {
        return this.pl$getBlock().pl$matchesStickyConditions(this.pl$asState(), neighborState, dir);
    }


    // BlockStateQuasiPower

    @Override
    default int pl$getQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return this.pl$getBlock().pl$getQuasiSignal(this.pl$asState(), level, pos, dir, dist);
    }

    @Override
    default int pl$getDirectQuasiSignal(BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return this.pl$getBlock().pl$getDirectQuasiSignal(this.pl$asState(), level, pos, dir, dist);
    }

    @Override
    default boolean pl$isQuasiConductor(BlockGetter level, BlockPos pos) {
        return this.pl$getBlock().pl$isQuasiConductor(this.pl$asState(), level, pos);
    }
}
