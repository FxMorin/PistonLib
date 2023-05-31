package ca.fxco.pistonlib.mixin.merging;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlabBlock.class)
public class SlabBlock_mergeMixin implements PLBlockBehaviour {

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return PistonLibConfig.doSlabMerging;
    }

    @Override
    public boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos,
                            BlockState mergingIntoState, Direction direction) {
        if (state.getBlock() != mergingIntoState.getBlock()) {
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
        return mergingIntoState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
    }

    //TODO: This is temporary for testing!
    // Slab blocks will need either half sticky blocks or half piston blocks to unmerge like this

    @Override
    public boolean pl$canUnMerge(BlockState state, BlockGetter level, BlockPos pos,
                              BlockState neighborState, Direction direction) {
        return state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE;
    }

    @Override
    public Pair<BlockState, BlockState> pl$doUnMerge(BlockState state, BlockGetter level,
                                                  BlockPos pos, Direction direction) {
        return new Pair<>(
                state.setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM),
                state.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP)
        );
    }
}
