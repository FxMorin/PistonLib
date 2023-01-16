package ca.fxco.pistonlib.impl;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
    default boolean canUnMerge(BlockState state, Direction dir) {
        return true;
    }


    //
    // These methods are called to determine what to do with the block entity when merging
    //

    /**
     * This method only gets called if this is the block entity of the block getting converted to a merge block
     * If false, the block entity is removed and the merge will finish like normally.
     */
    default boolean doMerging() {
        return true;
    }

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
    // These methods are called when merging, think of them more as events
    //

    default void onMerge(MergeBlockEntity mergeBlockEntity, Direction direction) {}

    /**
     * When the merge is done, this method will be called for all saved block entities.
     * This is used to modify the block entity. If no block entity is present to modify, this method is not called!
     */
    default void onAdvancedFinalMerge(BlockEntity blockEntity) {}
}
