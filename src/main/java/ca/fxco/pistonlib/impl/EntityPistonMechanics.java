package ca.fxco.pistonlib.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface EntityPistonMechanics {

    /**
     * Use this to prevent the entity from triggering the push into block calls/checks
     * This does not change any physics, just the API calls.
     * This will also prevent `onPistonCrushing()` from getting the block that its being crushed against
     */
    default boolean canPushIntoBlocks() {
        return false;
    }

    /**
     * If `canPushIntoBlocks` is enabled. When the entity is pushed into a block, this method is called.
     * Returns true if the block should also run its check against the entity.
     */
    default boolean onPushedIntoBlock(BlockState state,  BlockPos pos) {
        return true;
    }

    /**
     * This is called when an entity is being pushed by a piston against a block.
     * This still runs when `canPushIntoBlocks` is false, although `crushedAgainst` will always be null.
     * If the entity is getting crushed against multiple blocks, `crushedAgainst` will be null
     */
    default void onPistonCrushing(@Nullable Block crushedAgainst) {}
}
