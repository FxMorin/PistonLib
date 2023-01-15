package ca.fxco.pistonlib.impl;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Block Entities cannot control block merging, that's done from the Block's
 *
 * This is for handling merging the block entities.
 * These methods are only called if the original block had a block entity, that block entity type will be used
 */
public interface BlockEntityMerging {

    /**
     * This method only gets called if this is the block entity of the block getting converted to a merge block
     * If false, the block entity is removed and the merge will finish like normally.
     */
    default boolean doMerging() {
        return true;
    }

    default void onMerge(MergeBlockEntity mergeBlockEntity, Direction direction) {}

    /**
     * Used for Advanced Final Merging. To use `onAdvancedFinalMerge` this method needs to return true.
     * This defines if this block entity should be saved in the merge block.
     * If it's not saved, the `onAdvancedFinalMerge` will not run for this block entity and the data from this
     * block entity will not be stored.
     */
    default boolean shouldStoreSelf(MergeBlockEntity mergeBlockEntity) {
        return false;
    }

    /**
     * When the merge is done, this method will be called for all saved block entities.
     * This is used to modify the block entity. If no block entity is present to modify, this method is not called!
     */
    default void onAdvancedFinalMerge(BlockEntity blockEntity) {}
}
