package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.interfaces.ILevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.core.Direction.*;

public class Utils {

    public static Direction applyFacing(Direction dir, Direction facing) {
        return switch(facing) {
            case DOWN -> switch(dir) {
                case UP -> SOUTH;
                case DOWN -> NORTH;
                case SOUTH -> UP;
                case NORTH -> DOWN;
                default -> dir.getOpposite();
            };
            case UP -> switch(dir) {
                case UP -> NORTH;
                case DOWN -> SOUTH;
                case SOUTH -> DOWN;
                case NORTH -> UP;
                default -> dir.getOpposite();
            };
            case NORTH -> dir;
            case SOUTH -> dir != DOWN && dir != UP ? dir.getOpposite() : dir;
            case WEST -> switch(dir) {
                case EAST -> SOUTH;
                case WEST -> NORTH;
                case UP, DOWN -> dir;
                case SOUTH -> WEST;
                case NORTH -> EAST;
            };
            case EAST -> switch(dir) {
                case EAST -> NORTH;
                case WEST -> SOUTH;
                case UP, DOWN -> dir;
                case SOUTH -> EAST;
                case NORTH -> WEST;
            };
        };
    }

    public static boolean hasNeighborSignalExceptFromFacing(Level level, BlockPos pos, Direction except) {
        for (Direction dir : Direction.values()) {
            if (dir != except && level.hasSignal(pos.relative(dir), dir)) {
                return true;
            }
        }

        return false;
    }

    public static boolean setBlockWithEntity(Level level, BlockPos blockPos, BlockState state,
                                             BlockEntity blockEntity, int flags) {
        ((ILevel)level).prepareBlockEntityPlacement(blockPos, state, blockEntity);
        return level.setBlock(blockPos, state, flags);
    }
}
