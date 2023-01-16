package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EraseBlock extends Block implements ConfigurablePistonMerging {
    public EraseBlock(Properties properties) {
        super(properties);
    }

    public boolean usesConfigurablePistonMerging() {
        return true;
    }


    // Returns the merged state
    public BlockState doMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }


    // This must return true if you want to be able to merge more than one block at a time using `canMultiMerge` & `doMultiMerge`
    public boolean canMultiMerge() {
        return true;
    }

    // While merging with a block, is this block able to merge with other blocks from other directions?
    public boolean canMultiMerge(BlockState state, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    // Returns the merged state
    public BlockState doMultiMerge(Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }
}
