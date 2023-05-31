package ca.fxco.api.pistonlib.block;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BlockPistonMerging {

    // Both blocks that are attempting to merge should have the same checks!


    // This must return true in order for the configurable piston merging to be used!
    boolean pl$usesConfigurablePistonMerging();


    // Returns if it will be able to merge both states together
    boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    boolean pl$canMergeFromSide(BlockState state, BlockGetter level, BlockPos pos, Direction pushDir);

    // Returns the merged state
    BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir);


    // This must return true if you want to be able to merge more than one block at a time using `canMultiMerge` & `doMultiMerge`
    boolean pl$canMultiMerge();

    // While merging with a block, is this block able to merge with other blocks from other directions?
    boolean pl$canMultiMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging);

    // Returns the merged state
    BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos, Map<Direction, BlockState> states, BlockState mergingIntoState);


    // Returns if it will be able to unmerge into two different states
    boolean pl$canUnMerge(BlockState state, BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir);

    // Returns the blockstates that it should unmerge into.
    // The first block in the pair is the block that will be pulled out
    @Nullable Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, BlockGetter level, BlockPos pos, Direction dir);

    /**
     * This method determines when the block entity should be used:
     * -     NEVER = Block entity will be skipped completely
     * -   MERGING = Block entity will be used to check merging conditions
     * - UNMERGING = Block entity will be used to check unmerging conditions
     * -    ALWAYS = Block entity will always be checked
     * State checks will always happen before block entity checks
     * Skipping won't get the block entity at all, this is done for performance reasons.
     * It allows us to quickly know if the block entity should be loaded and checked against
     */
    MergeRule pl$getBlockEntityMergeRules();

    enum MergeRule {
        NEVER(false, false),
        MERGING(true, false),
        UNMERGING(false, true),
        ALWAYS(true, true);

        private final boolean merging;
        private final boolean unmerging;

        MergeRule(boolean merging, boolean unmerging) {
            this.merging = merging;
            this.unmerging = unmerging;
        }

        public boolean checkMerge() {
            return this.merging;
        }

        public boolean checkUnMerge() {
            return this.unmerging;
        }
    }
}
