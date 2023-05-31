package ca.fxco.api.pistonlib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * PistonTicking
 *
 * Allows blocks to run code every tick while being moved by a piston
 * Can be used on Blocks & BlockEntities
 * The `tickingApi` config option must be enabled in order to use any of the features provided here
 */
public interface MovingTickable {

    void pl$movingTick(Level level, BlockState state, BlockPos toPos, Direction dir, float progress, float speed, boolean merging);

}
