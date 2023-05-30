package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.block.BlockQuasiPower;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.api.pistonlib.block.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public abstract class Block_customBehaviorMixin extends BlockBehaviour implements ConfigurablePistonBehavior, ConfigurablePistonStickiness, ConfigurablePistonMerging, BlockQuasiPower {

    public Block_customBehaviorMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getQuasiSignal(BlockState blockState, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return getSignal(blockState, blockGetter, pos, dir);
    }

    @Override
    public int getDirectQuasiSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir, int dist) {
        return getDirectSignal(state, blockGetter, pos, dir);
    }

    @Override
    public boolean isQuasiConductor(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return state.isRedstoneConductor(blockGetter, blockPos);
    }
}
