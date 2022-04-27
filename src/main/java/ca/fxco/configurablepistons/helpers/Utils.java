package ca.fxco.configurablepistons.helpers;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Utils {
    public static Box stretchBlockBound(Box box, Direction direction, double length) {
        double d = length * (double)direction.getDirection().offset();
        double e = Math.min(d, 0.0);
        double f = Math.max(d, 0.0);
        return switch (direction) {
            case WEST -> new Box(Math.ceil(box.minX + e), box.minY, box.minZ, Math.floor(box.minX + f), box.maxY, box.maxZ);
            case EAST -> new Box(Math.ceil(box.maxX + e), box.minY, box.minZ, Math.floor(box.maxX + f), box.maxY, box.maxZ);
            case DOWN -> new Box(box.minX, Math.ceil(box.minY + e), box.minZ, box.maxX, Math.floor(box.minY + f), box.maxZ);
            case UP -> new Box(box.minX, Math.ceil(box.maxY + e), box.minZ, box.maxX, Math.floor(box.maxY + f), box.maxZ);
            case NORTH -> new Box(box.minX, box.minY, Math.ceil(box.minZ + e), box.maxX, box.maxY, Math.floor(box.minZ + f));
            case SOUTH -> new Box(box.minX, box.minY, Math.ceil(box.maxZ + e), box.maxX, box.maxY, Math.floor(box.maxZ + f));
        };
    }
}
