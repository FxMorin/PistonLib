package ca.fxco.configurablepistons.helpers;

import java.util.HashMap;
import java.util.Map;

import ca.fxco.configurablepistons.pistonLogic.StickyType;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HalfBlockUtils {

    public static final Map<Direction, StickyType>[] SIDES_LIST = Util.make(() -> {
        Direction[] dirs = Direction.values();
        @SuppressWarnings("unchecked")
        Map<Direction, StickyType>[] maps = new HashMap[dirs.length];

        for (int i = 0; i < dirs.length; i++) {
            Direction dir = dirs[i];

            maps[i] = Util.make(new HashMap<>(), map -> {
                map.put(Utils.applyFacing(Direction.UP, dir), StickyType.STICKY);
                map.put(Utils.applyFacing(Direction.DOWN, dir), StickyType.DEFAULT);
                map.put(Utils.applyFacing(Direction.NORTH, dir), StickyType.CONDITIONAL);
                map.put(Utils.applyFacing(Direction.SOUTH, dir), StickyType.CONDITIONAL);
                map.put(Utils.applyFacing(Direction.EAST, dir), StickyType.CONDITIONAL);
                map.put(Utils.applyFacing(Direction.WEST, dir), StickyType.CONDITIONAL);
            });
        }

        return maps;
    });

    protected static final VoxelShape UP_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
    protected static final VoxelShape EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);

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

    public static boolean isOnFacingSide(BlockGetter level, Vec3 entityPos, BlockPos blockPos) {
        return isOnFacingSide(entityPos, blockPos, level.getBlockState(blockPos));
    }

    public static boolean isOnFacingSide(Vec3 entityPos, BlockPos blockPos, BlockState state) {
        return switch(state.getValue(BlockStateProperties.FACING)) {
            case DOWN  -> true;
            case UP    -> false;
            case NORTH -> entityPos.z() - blockPos.getCenter().z() >= 0;
            case SOUTH -> entityPos.z() - blockPos.getCenter().z() <= 0;
            case WEST  -> entityPos.x() - blockPos.getCenter().x() >= 0;
            case EAST  -> entityPos.x() - blockPos.getCenter().x() <= 0;
        };
    }
}
