package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.base.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ObsidianSlabBlock extends SlabBlock {

    public ObsidianSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return true;
    }

    @Override
    public boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos,
                            BlockState mergingIntoState, Direction direction) {
        if (state.getBlock() != mergingIntoState.getBlock() && state.getBlock() != Blocks.SMOOTH_STONE_SLAB) {
            return false;
        }
        SlabType type1 = state.getValue(BlockStateProperties.SLAB_TYPE);
        SlabType type2 = mergingIntoState.getValue(BlockStateProperties.SLAB_TYPE);
        if (type1 == type2 || type1 == SlabType.DOUBLE || type2 == SlabType.DOUBLE) {
            return false;
        }
        if (direction == Direction.UP) {
            return type2 != SlabType.BOTTOM && type1 == SlabType.BOTTOM;
        } else if (direction == Direction.DOWN) {
            return type2 != SlabType.TOP && type1 == SlabType.TOP;
        }
        return true;
    }

    @Override
    public BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos,
                              BlockState mergingIntoState, Direction direction) {
        if (state.getBlock() == Blocks.SMOOTH_STONE_SLAB) {
            if (state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
                return ModBlocks.HALF_OBSIDIAN_BLOCK.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
            } else {
                return ModBlocks.HALF_OBSIDIAN_BLOCK.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN);
            }
        }
        return mergingIntoState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
    }
}
