package ca.fxco.configurablepistons.helpers;

import ca.fxco.configurablepistons.pistonLogic.StickyType;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class HalfBlockUtils {

    public static final Map<Direction, StickyType>[] SIDES_LIST = generateSides();

    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public static VoxelShape getSlabShape(Direction facing) {
        return switch(facing) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @SuppressWarnings("unchecked")
    public static Map<Direction, StickyType>[] generateSides() {
        Direction[] directions = Direction.values();
        Map<Direction, StickyType>[] maps = new HashMap[directions.length];
        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            maps[i] = new HashMap<>() {{
                put(Utils.applyFacing(Direction.UP, dir), StickyType.STICKY);
                put(Utils.applyFacing(Direction.DOWN, dir), StickyType.DEFAULT);
                put(Utils.applyFacing(Direction.NORTH, dir), StickyType.CONDITIONAL);
                put(Utils.applyFacing(Direction.SOUTH, dir), StickyType.CONDITIONAL);
                put(Utils.applyFacing(Direction.EAST, dir), StickyType.CONDITIONAL);
                put(Utils.applyFacing(Direction.WEST, dir), StickyType.CONDITIONAL);
            }};
        }
        return maps;
    }
}
