package ca.fxco.pistonlib.mixin.quasi;

import ca.fxco.pistonlib.impl.BlockStateQuasiPower;
import ca.fxco.pistonlib.impl.QLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class Level_quasiMixin implements QLevel {

    @Shadow public abstract BlockState getBlockState(BlockPos blockPos);

    @Shadow public abstract int getDirectSignalTo(BlockPos blockPos);

    @Override
    public int getStrongestQuasiNeighborSignal(BlockPos blockPos, int dist) {
        int strongest = Redstone.SIGNAL_NONE;
        for (Direction dir : Direction.values()) {
            int strength = this.getQuasiSignal(blockPos.relative(dir), dir, dist);
            if (strength >= Redstone.SIGNAL_MAX) {
                return Redstone.SIGNAL_MAX;
            } else if (strength > strongest) {
                strongest = strength;
            }
        }
        return strongest;
    }

    @Override
    public int getQuasiSignal(BlockPos blockPos, Direction direction, int dist) {
        BlockState blockState = this.getBlockState(blockPos);
        int i = ((BlockStateQuasiPower)blockState).getQuasiSignal((Level)(Object)this, blockPos, direction, dist);
        return blockState.isRedstoneConductor((Level)(Object)this, blockPos) ?
                Math.max(i, this.getDirectSignalTo(blockPos)) : i;
    }

    @Override
    public boolean hasQuasiNeighborSignal(BlockPos blockPos, int dist) {
        return this.hasQuasiSignal(blockPos.below(), Direction.DOWN, dist) ||
                this.hasQuasiSignal(blockPos.above(), Direction.UP, dist) ||
                this.hasQuasiSignal(blockPos.north(), Direction.NORTH, dist) ||
                this.hasQuasiSignal(blockPos.south(), Direction.SOUTH, dist) ||
                this.hasQuasiSignal(blockPos.west(), Direction.WEST, dist) ||
                this.hasQuasiSignal(blockPos.east(), Direction.EAST, dist);
    }

    @Override
    public boolean hasQuasiSignal(BlockPos blockPos, Direction direction, int dist) {
        BlockState blockState = this.getBlockState(blockPos);
        return ((BlockStateQuasiPower)blockState).hasQuasiSignal((Level)(Object)this, blockPos, direction, dist) ||
                (blockState.isRedstoneConductor((Level)(Object)this, blockPos) && this.getDirectSignalTo(blockPos) > 0);
    }
}
