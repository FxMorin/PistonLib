package ca.fxco.pistonlib.pistonLogic.accessible;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ConfigurablePistonBehavior {

    /*
     * These methods are only used if `usesConfigurablePistonBehavior` returns true
     * This allows for more configurable & conditional piston behavior
     */

    // This must return true in order for the configurable piston behavior to be used!
    default boolean usesConfigurablePistonBehavior() {
        return false;
    }

    // If the block is currently movable, for quick checks to boost performance by skipping more intensive checks early
    // However this is not always checked first in some instances, so make sure to account for that!
    default boolean isMovable(Level level, BlockPos pos, BlockState state) {
        return true;
    }

    default boolean canPistonPush(Level level, BlockPos pos, BlockState state, Direction direction) {
        return true;
    }

    default boolean canPistonPull(Level level, BlockPos pos, BlockState state, Direction direction) {
        return true;
    }

    default boolean canBypassFused(BlockState state) {
        return false;
    }

    default boolean canDestroy(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    // This is called whenever an entity is pushed into a block by a piston.
    default void onPushEntityInto(Level level, BlockPos pos, BlockState state, Entity entity) {}
}
