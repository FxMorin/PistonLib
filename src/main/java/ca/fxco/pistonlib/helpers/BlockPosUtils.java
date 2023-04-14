package ca.fxco.pistonlib.helpers;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;

@UtilityClass
public class BlockPosUtils {

    public static boolean isBetween(BlockPos pos, BlockPos start, BlockPos end) {
        return pos.getX() >= Math.min(start.getX(), end.getX()) && pos.getX() <= Math.max(start.getX(), end.getX()) &&
                pos.getY() >= Math.min(start.getY(), end.getY()) && pos.getY() <= Math.max(start.getY(), end.getY()) &&
                pos.getZ() >= Math.min(start.getZ(), end.getZ()) && pos.getZ() <= Math.max(start.getZ(), end.getZ());
    }

    public static boolean isNotBetween(BlockPos pos, BlockPos start, BlockPos end) {
        return pos.getX() < Math.min(start.getX(), end.getX()) || pos.getX() > Math.max(start.getX(), end.getX()) ||
                pos.getY() < Math.min(start.getY(), end.getY()) || pos.getY() > Math.max(start.getY(), end.getY()) ||
                pos.getZ() < Math.min(start.getZ(), end.getZ()) || pos.getZ() > Math.max(start.getZ(), end.getZ());
    }

    /**
     * Same as isBetween except the min and max are pre-defined
     */
    public static boolean isWithin(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX() &&
                pos.getY() >= min.getY() && pos.getY() <= max.getY() &&
                pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    /**
     * Same as isNotBetween except the min and max are pre-defined
     */
    public static boolean isNotWithin(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() < min.getX() || pos.getX() > max.getX() ||
                pos.getY() < min.getY() || pos.getY() > max.getY() ||
                pos.getZ() < min.getZ() || pos.getZ() > max.getZ();
    }
}
