package ca.fxco.pistonlib.blocks;

import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ObsidianSlabBlock extends SlabBlock implements ConfigurablePistonMerging {

    public ObsidianSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean usesConfigurablePistonMerging() {
        return true;
    }

    @Override
    public boolean canMerge(BlockState state, BlockPos blockPos, BlockState mergingIntoState, Direction dir) {
        if (state.getBlock() != mergingIntoState.getBlock() && state.getBlock() != Blocks.SMOOTH_STONE_SLAB) {
            return false;
        }
        SlabType type1 = state.getValue(BlockStateProperties.SLAB_TYPE);
        SlabType type2 = mergingIntoState.getValue(BlockStateProperties.SLAB_TYPE);
        if (type1 == type2 || type1 == SlabType.DOUBLE || type2 == SlabType.DOUBLE) {
            return false;
        }
        if (dir == Direction.UP) {
            return type2 != SlabType.BOTTOM && type1 == SlabType.BOTTOM;
        } else if (dir == Direction.DOWN) {
            return type2 != SlabType.TOP && type1 == SlabType.TOP;
        }
        return true;
    }

    @Override
    public BlockState doMerge(BlockState state, BlockPos blockPos, BlockState mergingIntoState, Direction dir) {
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
