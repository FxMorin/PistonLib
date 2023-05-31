package ca.fxco.pistonlib.mixin.merging;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.blocks.mergeBlock.MergeBlockEntity;
import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(IceBlock.class)
public class IceBlock_compressMixin implements PLBlockBehaviour {

    @Override
    public boolean pl$usesConfigurablePistonMerging() {
        return PistonLibConfig.doIceMerging;
    }

    @Override
    public boolean pl$canMultiMerge() {
        return true;
    }

    @Override
    public boolean pl$canMerge(BlockState state, BlockGetter level, BlockPos pos,
                            BlockState mergingIntoState, Direction direction) {
        return state.getBlock() == mergingIntoState.getBlock();
    }

    @Override
    public boolean pl$canMultiMerge(BlockState state, BlockGetter getter, BlockPos pos, BlockState mergingIntoState,
                                 Direction direction, Map<Direction, MergeBlockEntity.MergeData> currentlyMerging) {
        return currentlyMerging.size() <= 2; // max 3
    }

    @Override
    public BlockState pl$doMerge(BlockState state, BlockGetter level, BlockPos pos,
                              BlockState mergingIntoState, Direction direction) {
        return Blocks.ICE.defaultBlockState();
    }

    @Override
    public BlockState pl$doMultiMerge(BlockGetter level, BlockPos pos,
                                   Map<Direction, BlockState> states, BlockState mergingIntoState) {
        if (states.size() != 3) {
            return Blocks.ICE.defaultBlockState();
        }
        for (BlockState state : states.values()) {
            if (state.getBlock() != mergingIntoState.getBlock()) {
                return Blocks.ICE.defaultBlockState();
            }
        }
        return Blocks.PACKED_ICE.defaultBlockState();
    }
}
