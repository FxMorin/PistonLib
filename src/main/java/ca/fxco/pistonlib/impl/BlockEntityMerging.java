package ca.fxco.pistonlib.impl;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is for handling merging the block entities.
 */
public interface BlockEntityMerging {

    //
    // These methods are to control if merging should happen
    //

    /**
     * Returns if it will be able to merge both states together
     */
    default boolean canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    /**
     * While merging with a block, is this block able to merge with other blocks from other directions?
     */
    default boolean canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    /**
     * Returns if it will be able to unmerge into two different states
     */
    default boolean canUnMerge(BlockState state, BlockState neighborState, Direction dir) {
        return true;
    }


    //
    // These methods are called to determine what to do with the block entity when merging
    //

    /**
     * Used for Advanced Final Merging. To use `onAdvancedFinalMerge` this method needs to return true.
     * This defines if this block entity should be saved in the merge block.
     * If it's not saved, the `onAdvancedFinalMerge` will not run for this block entity and the data from this
     * block entity will not be stored.
     */
    default boolean shouldStoreSelf(MergeBlockEntity mergeBlockEntity) {
        return false;
    }


    //
    // These methods are called when merging start (onMerge) and merging end (onAdvancedFinalMerge)
    // They are only called for blocks merging into another block. Not initial blocks
    //

    default void onMerge(MergeBlockEntity mergeBlockEntity, Direction direction) {}

    /**
     * When the merge is done, this method will be called for all saved block entities.
     * This is used to modify the block entity. If no block entity is present to modify, this method is not called!
     */
    default void onAdvancedFinalMerge(BlockEntity blockEntity) {}


    //
    // These methods are called when unmerging
    //

    /**
     * Determines what blocks the block entity should unmerge into.
     * Return null to call the blockstate `doUnMerge` method
     * The first block in the pair is the block that will be pulled out
     */
    default @Nullable Pair<BlockState, BlockState> doUnMerge(BlockState state, Direction direction) {
        return null;
    }

    /**
     * If the block entity should unmerge also or if it should stay where it is.
     * If it stays it won't replace the block entity
     */
    default boolean shouldUnMergeBlockEntity(BlockState state, Direction direction) {
        return true;
    }


    //
    // These methods are only for initial block entities. For blocks that got other blocks merged into them
    //

    /**
     * This method only gets called if this is the block entity of the block getting converted to a merge block
     * If false, the block entity is removed and the merge will finish like normally.
     */
    default boolean doInitialMerging() {
        return true;
    }

    /**
     * When the merge is done, before all the other `onAdvancedFinalMerge` methods
     */
    default void beforeInitialFinalMerge(BlockState finalState, Map<Direction, MergeBlockEntity.MergeData> mergedData) {}

    /**
     * When the merge is done, after all the other `onAdvancedFinalMerge` methods
     */
    default void afterInitialFinalMerge(BlockState finalState, Map<Direction, MergeBlockEntity.MergeData> mergedData) {}
}
