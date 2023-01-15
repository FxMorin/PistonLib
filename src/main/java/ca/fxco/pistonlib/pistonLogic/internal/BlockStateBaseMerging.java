package ca.fxco.pistonlib.pistonLogic.internal;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface BlockStateBaseMerging {

    // These methods are only used if `usesConfigurablePistonMerging` return true
    // This allows for configurable & conditional mering/compression block logic
    boolean usesConfigurablePistonMerging();

    boolean canMultiMerge();

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    boolean canMergeFromSide(Direction pushDirection);

    // Returns if it will be able to merge both states together
    boolean canMerge(BlockState mergingIntoState, Direction dir);

    // While merging with a block, is this block able to merge with other blocks from other directions?
    boolean canMultiMerge(BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging);

    // Returns the merged state
    BlockState doMerge(BlockState mergingIntoState, Direction dir);

    // Returns the merged state
    BlockState doMultiMerge(Map<Direction,BlockState> states, BlockState mergingIntoState);
}
