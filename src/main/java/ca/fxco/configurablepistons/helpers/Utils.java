package ca.fxco.configurablepistons.helpers;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Utils {

    public static double ceil(double ceil) {
        return Math.ceil(ceil);
    }

    public static double floor(double floor) {
        return Math.floor(floor);
    }

    public static Box stretchBlockBound(Box box, Direction direction, double length) {
        double d = length * (double)direction.getDirection().offset();
        double e = Math.min(d, 0.0);
        double f = Math.max(d, 0.0);
        return switch (direction) {
            case WEST ->  new Box(ceil(box.minX + e), box.minY, box.minZ, floor(box.minX + f), box.maxY, box.maxZ);
            case EAST ->  new Box(ceil(box.maxX + e), box.minY, box.minZ, floor(box.maxX + f), box.maxY, box.maxZ);
            case DOWN ->  new Box(box.minX, ceil(box.minY + e), box.minZ, box.maxX, floor(box.minY + f), box.maxZ);
            case UP ->    new Box(box.minX, ceil(box.maxY + e), box.minZ, box.maxX, floor(box.maxY + f), box.maxZ);
            case NORTH -> new Box(box.minX, box.minY, ceil(box.minZ + e), box.maxX, box.maxY, floor(box.minZ + f));
            case SOUTH -> new Box(box.minX, box.minY, ceil(box.maxZ + e), box.maxX, box.maxY, floor(box.maxZ + f));
        };
    }
}
