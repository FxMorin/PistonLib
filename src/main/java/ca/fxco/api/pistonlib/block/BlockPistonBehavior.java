package ca.fxco.api.pistonlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockPistonBehavior {

    int pl$getWeight(BlockState state);

    /*
     * These methods are only used if `usesConfigurablePistonBehavior` returns true
     * This allows for more configurable & conditional piston behavior
     */

    // This must return true in order for the configurable piston behavior to be used!
    boolean pl$usesConfigurablePistonBehavior();

    // If the block is currently movable, for quick checks to boost performance by skipping more intensive checks early
    // However this is not always checked first in some instances, so make sure to account for that!
    boolean pl$isMovable(Level level, BlockPos pos, BlockState state);

    boolean pl$canPistonPush(Level level, BlockPos pos, BlockState state, Direction dir);

    boolean pl$canPistonPull(Level level, BlockPos pos, BlockState state, Direction dir);

    boolean pl$canBypassFused(BlockState state);

    boolean pl$canDestroy(Level level, BlockPos pos, BlockState state);

    // This is called whenever an entity is pushed into a block by a piston.
    void pl$onPushEntityInto(Level level, BlockPos pos, BlockState state, Entity entity);

}
