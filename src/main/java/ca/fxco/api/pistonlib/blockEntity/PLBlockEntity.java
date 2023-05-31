package ca.fxco.api.pistonlib.blockEntity;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface collects all custom behavior that is injected into {@code BlockEntity}s.
 * It provides default implementations for all custom behavior.
 */
public interface PLBlockEntity extends BlockEntityPistonMerging, BlockEntityPostLoad {


    // BlockEntityPistonMerging

    @Override
    default boolean pl$canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    @Override
    default boolean pl$canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    @Override
    default boolean pl$canUnMerge(BlockState state, BlockState neighborState, Direction dir) {
        return true;
    }

    @Override
    default boolean pl$shouldStoreSelf(MergeBlockEntity mergeBlockEntity) {
        return false;
    }

    @Override
    default void pl$onMerge(MergeBlockEntity mergeBlockEntity, Direction dir) {
    }

    @Override
    default void pl$onAdvancedFinalMerge(BlockEntity blockEntity) {
    }

    @Override
    default @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, Direction dir) {
        return null;
    }

    @Override
    default boolean pl$shouldUnMergeBlockEntity(BlockState state, Direction dir) {
        return true;
    }

    @Override
    default boolean pl$doInitialMerging() {
        return true;
    }

    @Override
    default void pl$beforeInitialFinalMerge(BlockState finalState, Map<Direction, MergeBlockEntity.MergeData> mergedData) {
    }

    @Override
    default void pl$afterInitialFinalMerge(BlockState finalState, Map<Direction, MergeBlockEntity.MergeData> mergedData) {
    }


    // BlockEntityPostLoad

    @Override
    default boolean pl$shouldPostLoad() {
        return false;
    }

    @Override
    default void pl$onPostLoad() {
        if (!this.pl$shouldPostLoad())
            throw new IllegalStateException("block entity should not post load but the post load callback was called anyway!");
    }
}
