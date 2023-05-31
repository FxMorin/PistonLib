package ca.fxco.pistonlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

public class WeakPoweredBlock extends PoweredBlock {

    public WeakPoweredBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int pl$getQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return Redstone.SIGNAL_MIN;
    }

    @Override
    public int pl$getDirectQuasiSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, int dist) {
        return Redstone.SIGNAL_MIN;
    }

    @Override
    public boolean pl$isQuasiConductor(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }
}
