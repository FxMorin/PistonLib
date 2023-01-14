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

    @Override
    public int getDirectQuasiSignalTo(BlockPos pos, int d) {
        int i = Redstone.SIGNAL_NONE;
        if ((i = Math.max(i, getDirectQuasiSignal(pos.below(), Direction.DOWN, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        } else if ((i = Math.max(i, getDirectQuasiSignal(pos.above(), Direction.UP, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        } else if ((i = Math.max(i, getDirectQuasiSignal(pos.north(), Direction.NORTH, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        } else if ((i = Math.max(i, getDirectQuasiSignal(pos.south(), Direction.SOUTH, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        } else if ((i = Math.max(i, getDirectQuasiSignal(pos.west(), Direction.WEST, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        return Math.max(i, getDirectQuasiSignal(pos.east(), Direction.EAST, d));
    }

    @Override
    public boolean hasDirectQuasiSignalTo(BlockPos pos, int d) {
        return getDirectQuasiSignal(pos.below(), Direction.DOWN, d) > 0 ||
                getDirectQuasiSignal(pos.above(), Direction.UP, d) > 0 ||
                getDirectQuasiSignal(pos.north(), Direction.NORTH, d) > 0 ||
                getDirectQuasiSignal(pos.south(), Direction.SOUTH, d) > 0 ||
                getDirectQuasiSignal(pos.west(), Direction.WEST, d) > 0 ||
                getDirectQuasiSignal(pos.east(), Direction.EAST, d) > 0;
    }

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
        BlockState state = this.getBlockState(blockPos);
        int i = ((BlockStateQuasiPower)state).getQuasiSignal((Level)(Object)this, blockPos, direction, dist);
        return ((BlockStateQuasiPower)state).isQuasiConductor((Level)(Object)this, blockPos) ?
                Math.max(i, this.getDirectQuasiSignalTo(blockPos, dist)) : i;
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
    public boolean hasQuasiSignal(BlockPos pos, Direction dir, int dist) {
        BlockStateQuasiPower quasiPower = (BlockStateQuasiPower)this.getBlockState(pos);
        return (quasiPower.hasQuasiSignal((Level)(Object)this, pos, dir, dist) ||
                (quasiPower.isQuasiConductor((Level)(Object)this, pos) && this.hasDirectQuasiSignalTo(pos, dist)));
    }

    @Override
    public int getDirectQuasiSignal(BlockPos pos, Direction dir, int dist) {
        return ((BlockStateQuasiPower)this.getBlockState(pos))
                .getDirectQuasiSignal((Level)(Object)this, pos, dir, dist);
    }
}
