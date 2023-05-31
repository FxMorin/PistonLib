package ca.fxco.api.pistonlib.block.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface BlockStatePistonBehavior {

    int pl$getWeight();

    /*
     * This interface is for internal use only. Use ConfigurablePistonBehavior for single block conditions
     */

    // These methods are only used if `usesConfigurablePistonBehavior` return true
    // This allows for more configurable & conditional piston behavior
    boolean pl$usesConfigurablePistonBehavior();

    boolean pl$isMovable(Level level, BlockPos pos);

    boolean pl$canPistonPush(Level level, BlockPos pos, Direction dir);

    boolean pl$canPistonPull(Level level, BlockPos pos, Direction dir);

    boolean pl$canBypassFused();

    boolean pl$canDestroy(Level level, BlockPos pos);

    void pl$onPushEntityInto(Level level, BlockPos pos, Entity entity);

}
