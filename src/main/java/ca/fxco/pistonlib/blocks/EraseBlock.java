package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EraseBlock extends Block implements ConfigurablePistonMerging {
    public EraseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean usesConfigurablePistonMerging() {
        return true;
    }


    @Override
    public BlockState doMerge(BlockState state, BlockPos blockPos, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState;
    }


    @Override
    public boolean canMultiMerge() {
        return true;
    }

    @Override
    public boolean canMultiMerge(BlockState state, BlockPos blockPos, BlockState mergingIntoState, Direction dir, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    @Override
    public BlockState doMultiMerge(BlockPos blockPos, Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }
}
