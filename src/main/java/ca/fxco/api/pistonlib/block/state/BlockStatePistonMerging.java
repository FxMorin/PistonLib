package ca.fxco.api.pistonlib.block.state;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.api.pistonlib.block.BlockPistonMerging;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BlockStatePistonMerging {

    // These methods are only used if `usesConfigurablePistonMerging` return true
    // This allows for configurable & conditional mering/compression block logic
    boolean pl$usesConfigurablePistonMerging();


    // Returns if it will be able to merge both states together
    boolean pl$canMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    boolean pl$canMergeFromSide(BlockGetter level, BlockPos pos, Direction pushDir);

    // Returns the merged state
    BlockState pl$doMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);


    boolean pl$canMultiMerge();

    // While merging with a block, is this block able to merge with other blocks from other directions?
    boolean pl$canMultiMerge(BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging);

    // Returns the merged state
    BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction,BlockState> states, BlockState mergingIntoState);


    // Returns if it will be able to unmerge into two different states
    boolean pl$canUnMerge(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir);

    // Returns the blockstates that it should unmerge into.
    // The first block in the pair is the block that will be pulled out
    @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockGetter level, BlockPos pos, Direction dir);

    // Read ConfigurablePistonMerging description
    BlockPistonMerging.MergeRule pl$getBlockEntityMergeRules();
}
