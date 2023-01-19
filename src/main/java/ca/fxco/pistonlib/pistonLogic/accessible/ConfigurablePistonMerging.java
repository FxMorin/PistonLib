package ca.fxco.pistonlib.pistonLogic.accessible;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
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
    default boolean canMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction dir) {
        return true;
    }

    // If the block can be merged from a given side or can be merged from that side. Usually opposite of pushDirection
    default boolean canMergeFromSide(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction pushDirection) {
        return true;
    }

    // Returns the merged state
    default BlockState doMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }


    // This must return true if you want to be able to merge more than one block at a time using `canMultiMerge` & `doMultiMerge`
    default boolean canMultiMerge() {
        return false;
    }

    // While merging with a block, is this block able to merge with other blocks from other directions?
    default boolean canMultiMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return false;
    }

    // Returns the merged state
    default BlockState doMultiMerge(BlockGetter blockGetter, BlockPos blockPos, Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }


    // Returns if it will be able to unmerge into two different states
    default boolean canUnMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos, BlockState neighborState, Direction dir) {
        return false;
    }

    // Returns the blockstates that it should unmerge into.
    // The first block in the pair is the block that will be pulled out
    default @Nullable Pair<BlockState, BlockState> doUnMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction dir) {
        return null;
    }

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
    default MergeRule getBlockEntityMergeRules() {
        return MergeRule.NEVER;
    }

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
