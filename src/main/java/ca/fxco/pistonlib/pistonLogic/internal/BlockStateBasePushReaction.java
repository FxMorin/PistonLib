package ca.fxco.pistonlib.pistonLogic.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface BlockStateBasePushReaction {

    /*
     * This interface is for internal use only. Use ConfigurablePistonBehavior for single block conditions
     */

    // These methods are only used if `usesConfigurablePistonBehavior` return true
    // This allows for more configurable & conditional piston behavior
    boolean usesConfigurablePistonBehavior();
    boolean isMovable(Level level, BlockPos pos);
    boolean canPistonPush(Level level, BlockPos pos, Direction direction);
    boolean canPistonPull(Level level, BlockPos pos, Direction direction);
    boolean canBypassFused();
    boolean canDestroy(Level level, BlockPos pos);
    void onPushEntityInto(Level level, BlockPos pos, Entity entity);
}
