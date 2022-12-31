package ca.fxco.configurablepistons.blocks.halfBlocks;

import ca.fxco.configurablepistons.pistonLogic.StickyGroup;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Map;

import static ca.fxco.configurablepistons.helpers.HalfBlockUtils.SIDES_LIST;
import static ca.fxco.configurablepistons.helpers.HalfBlockUtils.getSlabShape;

public class HalfHoneyBlock extends HoneyBlock implements ConfigurablePistonStickiness {

    public static final DirectionProperty FACING = Properties.FACING;
    protected static final VoxelShape[] COLLISION_SHAPES = generateShapes();

    public HalfHoneyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES[state.get(FACING).ordinal()];
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return getSlabShape(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return SIDES_LIST[state.get(FACING).ordinal()];
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction direction) {
        Direction facing = state.get(FACING);
        if (direction == facing) { // front
            return StickyType.STICKY;
        } else if (direction == facing.getOpposite()) {
            return StickyType.DEFAULT;
        }
        return StickyType.CONDITIONAL;
    }

    // Only the sides call the conditional check
    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState adjState, Direction direction) {
        if (adjState.getBlock() == this) { // Block attempting to stick another half honey block
            Direction facing = state.get(FACING);
            Direction adjFacing = adjState.get(FACING);
            return facing == adjFacing || facing.getOpposite() != adjFacing;
        }
        StickyGroup group = ((ConfigurablePistonStickiness)adjState.getBlock()).getStickyGroup();
        if (group != null) {
            return StickyGroup.canStick(StickyGroup.HONEY, group);
        }
        return false;
    }

    public static VoxelShape[] generateShapes() {
        Direction[] directions = Direction.values();
        VoxelShape[] shapes = new VoxelShape[directions.length];
        for (int i = 0; i < directions.length; i++) {
            shapes[i] = VoxelShapes.union(SHAPE, getSlabShape(directions[i]));
        }
        return shapes;
    }
}
