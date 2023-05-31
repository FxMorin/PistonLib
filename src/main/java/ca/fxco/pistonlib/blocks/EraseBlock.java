package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EraseBlock extends Block {
    public EraseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return true;
    }


    @Override
    public BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos,
                              BlockState mergingIntoState, Direction direction) {
        return mergingIntoState;
    }


    @Override
    public boolean pl$canMultiMerge() {
        return true;
    }

    @Override
    public boolean pl$canMultiMerge(BlockState state, BlockGetter getter, BlockPos pos, BlockState mergingIntoState,
                                 Direction direction, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return true;
    }

    @Override
    public BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos,
                                   Map<Direction, BlockState> states, BlockState mergingIntoState) {
        return mergingIntoState;
    }
}
