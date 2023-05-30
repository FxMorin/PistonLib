package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.api.pistonlib.block.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
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
    public BlockState doMerge(BlockState state, BlockGetter blockGetter, BlockPos blockPos,
                              BlockState mergingIntoState, Direction direction) {
        return mergingIntoState;
    }


    @Override
    public boolean canMultiMerge() {
        return true;
    }

    @Override
    public boolean canMultiMerge(BlockState state, BlockGetter getter, BlockPos blockPos, BlockState mergingIntoState,
                                 Direction direction, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    @Override
    public BlockState doMultiMerge(BlockGetter blockGetter, BlockPos blockPos,
                                   Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }
}
