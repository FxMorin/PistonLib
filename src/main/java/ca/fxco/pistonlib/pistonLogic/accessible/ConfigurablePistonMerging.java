package ca.fxco.pistonlib.pistonLogic.accessible;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface ConfigurablePistonMerging {

    // Both blocks that are attempting to merge should have the same checks!


    // This must return true in order for the configurable piston merging to be used!
    default boolean usesConfigurablePistonMerging() {
        return false;
    }

    // This must return true if you want to be able to merge more than one block at a time using `canMultiMerge` & `doMultiMerge`
    default boolean canMultiMerge() {
        return false;
    }

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    default boolean canMergeFromSide(BlockState state, Direction pushDirection) {
        return true;
    }

    // Returns if it will be able to merge both states together
    default boolean canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    // While merging with a block, is this block able to merge with other blocks from other directions?
    default boolean canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return false;
    }

    // Returns the merged state
    default BlockState doMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return Blocks.AIR.defaultBlockState();
    }

    // Returns the merged state
    default BlockState doMultiMerge(Map<Direction,BlockState> states, BlockState mergingIntoState) {
        return Blocks.AIR.defaultBlockState();
    }

    //TODO: add a way to cancel merging or needed conditions for successful merging
}
