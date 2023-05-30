package ca.fxco.pistonlib.blocks;

import ca.fxco.api.pistonlib.block.BlockQuasiPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WeakPoweredBlock extends PoweredBlock implements BlockQuasiPower {

    public WeakPoweredBlock(Properties properties) {
        super(properties);
    }


    @Override
    public int getQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    public boolean hasQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return false;
    }

    @Override
    public int getDirectQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return 0;
    }

    @Override
    public boolean isQuasiConductor(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }
}
