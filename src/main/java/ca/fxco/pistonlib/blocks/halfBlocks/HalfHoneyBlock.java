package ca.fxco.pistonlib.blocks.halfBlocks;

import java.util.Map;

import ca.fxco.pistonlib.base.ModStickyGroups;
import ca.fxco.api.pistonlib.block.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickRules;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static ca.fxco.pistonlib.helpers.HalfBlockUtils.SIDES_LIST;
import static ca.fxco.pistonlib.helpers.HalfBlockUtils.getSlabShape;

public class HalfHoneyBlock extends HoneyBlock implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape[] COLLISION_SHAPES = Util.make(() -> {
        Direction[] dirs = Direction.values();
        VoxelShape[] shapes = new VoxelShape[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            shapes[i] = Shapes.or(SHAPE, getSlabShape(dirs[i]));
        }
        return shapes;
    });

    public HalfHoneyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return COLLISION_SHAPES[state.getValue(FACING).ordinal()];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getSlabShape(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return SIDES_LIST[state.getValue(FACING).ordinal()];
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        Direction facing = state.getValue(FACING);
        if (dir == facing) { // front
            return StickyType.STICKY;
        } else if (dir == facing.getOpposite()) { // back
            return StickyType.DEFAULT;
        }
        return StickyType.CONDITIONAL;
    }

    // Only the sides call the conditional check
    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        if (neighborState.is(this)) { // Block attempting to stick another half honey block
            Direction facing = state.getValue(FACING);
            Direction neighborFacing = neighborState.getValue(FACING);
            return facing == neighborFacing || facing.getOpposite() != neighborFacing;
        }
        StickyGroup group = ((ConfigurablePistonStickiness)neighborState.getBlock()).getStickyGroup();
        if (group != null) {
            return StickRules.test(ModStickyGroups.HONEY, group);
        }
        return true;
    }
}
