package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.impl.ILevel;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;

import static net.minecraft.core.Direction.*;

@UtilityClass
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

    // Ya pretend this is not here xD
    public static DyeColor properDyeMixing(DyeColor col1, DyeColor col2) {
        if (col1.equals(col2)) return col1;
        return switch(col1) {
            case WHITE -> switch(col2) {
                    case BLUE -> DyeColor.LIGHT_BLUE;
                    case GRAY -> DyeColor.LIGHT_GRAY;
                    case BLACK -> DyeColor.GRAY;
                    case GREEN -> DyeColor.LIME;
                    case RED -> DyeColor.PINK;
                    default -> col1;
                };
            case BLUE -> switch(col2) {
                    case WHITE -> DyeColor.LIGHT_BLUE;
                    case GREEN -> DyeColor.CYAN;
                    case RED -> DyeColor.PURPLE;
                    default -> col1;
                };
            case RED -> switch(col2) {
                    case YELLOW -> DyeColor.ORANGE;
                    case WHITE -> DyeColor.PINK;
                    case BLUE -> DyeColor.PURPLE;
                    default -> col1;
                };
            case GREEN -> switch(col2) {
                    case BLUE -> DyeColor.CYAN;
                    case WHITE -> DyeColor.LIME;
                    default -> col1;
                };
            case YELLOW -> col2.equals(DyeColor.RED) ? DyeColor.ORANGE : col1;
            case PURPLE -> col2.equals(DyeColor.PINK) ? DyeColor.MAGENTA : col1;
            case PINK -> col2.equals(DyeColor.PURPLE) ? DyeColor.MAGENTA : col1;
            case GRAY -> col2.equals(DyeColor.WHITE) ? DyeColor.LIGHT_GRAY : col1;
            case BLACK -> col2.equals(DyeColor.WHITE) ? DyeColor.GRAY : col1;
            default -> col1;
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
