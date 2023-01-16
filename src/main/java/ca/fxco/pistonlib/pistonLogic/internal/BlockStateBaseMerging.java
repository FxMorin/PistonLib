package ca.fxco.pistonlib.pistonLogic.internal;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BlockStateBaseMerging {

    // These methods are only used if `usesConfigurablePistonMerging` return true
    // This allows for configurable & conditional mering/compression block logic
    boolean usesConfigurablePistonMerging();


    // Returns if it will be able to merge both states together
    boolean canMerge(BlockPos blockPos, BlockState mergingIntoState, Direction dir);

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    boolean canMergeFromSide(BlockPos blockPos, Direction pushDirection);

    // Returns the merged state
    BlockState doMerge(BlockPos blockPos, BlockState mergingIntoState, Direction dir);


    boolean canMultiMerge();

    // While merging with a block, is this block able to merge with other blocks from other directions?
    boolean canMultiMerge(BlockPos blockPos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging);

    // Returns the merged state
    BlockState doMultiMerge(BlockPos blockPos, Map<Direction,BlockState> states, BlockState mergingIntoState);


    // Returns if it will be able to unmerge into two different states
    boolean canUnMerge(BlockPos blockPos, Direction dir);

    // Returns the blockstates that it should unmerge into.
    // The first block in the pair is the block that will be pulled out
    @Nullable Pair<BlockState, BlockState> doUnMerge(BlockPos blockPos, BlockState pistonBlockState, Direction dir);
}
