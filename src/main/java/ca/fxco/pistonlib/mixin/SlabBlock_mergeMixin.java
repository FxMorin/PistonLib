package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SlabBlock.class)
public class SlabBlock_mergeMixin implements ConfigurablePistonMerging {

    @Override
    public boolean usesConfigurablePistonMerging() {
        return true;
    }

    @Override
    public boolean canMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        if (state.getBlock() != mergingIntoState.getBlock()) {
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
    public BlockState doMerge(BlockState state, BlockState mergingIntoState, Direction dir) {
        return mergingIntoState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
    }
}
