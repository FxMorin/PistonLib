package ca.fxco.pistonlib.pistonLogic.accessible;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ConfigurablePistonMerging {

    // Both blocks that are attempting to merge should have the same checks!


    // This must return true in order for the configurable piston merging to be used!
    default boolean usesConfigurablePistonMerging() {
        return false;
    }


    // Returns if it will be able to merge both states together
    default boolean canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    default boolean canMergeFromSide(BlockState state, Direction pushDirection) {
        return true;
    }

    // Returns the merged state
    default BlockState doMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }


    // This must return true if you want to be able to merge more than one block at a time using `canMultiMerge` & `doMultiMerge`
    default boolean canMultiMerge() {
        return false;
    }

    // While merging with a block, is this block able to merge with other blocks from other directions?
    default boolean canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return false;
    }

    // Returns the merged state
    default BlockState doMultiMerge(Map<Direction,BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }


    // Returns if it will be able to unmerge into two different states
    default boolean canUnMerge(BlockState state, Direction dir) {
        return false;
    }

    // Returns the blockstates that it should unmerge into.
    // The first block in the pair is the block that will be pulled out
    default @Nullable Pair<BlockState, BlockState> doUnMerge(BlockState state, BlockState pistonBlockState, Direction dir) {
        return null;
    }
}
