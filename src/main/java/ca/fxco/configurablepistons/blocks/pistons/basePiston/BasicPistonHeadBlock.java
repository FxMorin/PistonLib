package ca.fxco.configurablepistons.blocks.pistons.basePiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamilies;
import ca.fxco.configurablepistons.pistonLogic.families.PistonFamily;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Arrays;

public class BasicPistonHeadBlock extends FacingBlock {
    public static final EnumProperty<PistonType> TYPE;
    public static final BooleanProperty SHORT;
    protected static final VoxelShape EAST_HEAD_SHAPE;
    protected static final VoxelShape WEST_HEAD_SHAPE;
    protected static final VoxelShape SOUTH_HEAD_SHAPE;
    protected static final VoxelShape NORTH_HEAD_SHAPE;
    protected static final VoxelShape UP_HEAD_SHAPE;
    protected static final VoxelShape DOWN_HEAD_SHAPE;
    protected static final VoxelShape UP_ARM_SHAPE;
    protected static final VoxelShape DOWN_ARM_SHAPE;
    protected static final VoxelShape SOUTH_ARM_SHAPE;
    protected static final VoxelShape NORTH_ARM_SHAPE;
    protected static final VoxelShape EAST_ARM_SHAPE;
    protected static final VoxelShape WEST_ARM_SHAPE;
    protected static final VoxelShape SHORT_UP_ARM_SHAPE;
    protected static final VoxelShape SHORT_DOWN_ARM_SHAPE;
    protected static final VoxelShape SHORT_SOUTH_ARM_SHAPE;
    protected static final VoxelShape SHORT_NORTH_ARM_SHAPE;
    protected static final VoxelShape SHORT_EAST_ARM_SHAPE;
    protected static final VoxelShape SHORT_WEST_ARM_SHAPE;
    private static final VoxelShape[] SHORT_HEAD_SHAPES;
    private static final VoxelShape[] HEAD_SHAPES;

    public static VoxelShape[] getHeadShapes(boolean shortHead) {
        return Arrays.stream(Direction.values()).map((dir) -> getHeadShape(dir, shortHead)).toArray(VoxelShape[]::new);
    }

    //TODO: Make PistonHeadBlock.getHeadShape() public and call it in here instead of re-initializing all this garbage
    public static VoxelShape getHeadShape(Direction direction, boolean shortHead) {
        return switch (direction) {
            default -> VoxelShapes.union(DOWN_HEAD_SHAPE, shortHead ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE);
            case UP -> VoxelShapes.union(UP_HEAD_SHAPE, shortHead ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE);
            case NORTH -> VoxelShapes.union(NORTH_HEAD_SHAPE, shortHead ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE);
            case SOUTH -> VoxelShapes.union(SOUTH_HEAD_SHAPE, shortHead ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE);
            case WEST -> VoxelShapes.union(WEST_HEAD_SHAPE, shortHead ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE);
            case EAST -> VoxelShapes.union(EAST_HEAD_SHAPE, shortHead ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE);
        };
    }

    public BasicPistonHeadBlock() {
        this(FabricBlockSettings.copyOf(Blocks.PISTON_HEAD));
    }

    public BasicPistonHeadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, PistonType.DEFAULT)
                .with(SHORT, false));
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (state.get(SHORT) ? SHORT_HEAD_SHAPES : HEAD_SHAPES)[state.get(FACING).ordinal()];
    }

    public boolean isAttached(BlockState headState, BlockState pistonState) {
        PistonFamily family = PistonFamilies.getFamily(this);
        Block of = headState.get(TYPE) == PistonType.DEFAULT ? family.getPistonBlock() : family.getStickyPistonBlock();
        return pistonState.isOf(of) && pistonState.get(BasicPistonBlock.EXTENDED) &&
                pistonState.get(FACING) == headState.get(FACING);
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.getAbilities().creativeMode) {
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            if (this.isAttached(state, world.getBlockState(blockPos))) world.breakBlock(blockPos, false);
        }
        super.onBreak(world, pos, state, player);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;
        super.onStateReplaced(state, world, pos, newState, moved);
        BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
        if (this.isAttached(state, world.getBlockState(blockPos))) world.breakBlock(blockPos, true);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ?
                Blocks.AIR.getDefaultState() :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return this.isAttached(state, blockState) || blockState.isIn(ModTags.MOVING_PISTONS) &&
                blockState.get(FACING) == state.get(FACING);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean n) {
        if (state.canPlaceAt(world, pos)) {
            BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
            world.getBlockState(blockPos).neighborUpdate(world, blockPos, block, fromPos, false);
        }
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        PistonFamily family = PistonFamilies.getFamily(this);
        return new ItemStack(state.get(TYPE) == PistonType.STICKY ?
                family.getStickyPistonBlock() : family.getPistonBlock());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    static {
        TYPE = Properties.PISTON_TYPE;
        SHORT = Properties.SHORT;
        EAST_HEAD_SHAPE = Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        WEST_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        SOUTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        NORTH_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        UP_HEAD_SHAPE = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        DOWN_HEAD_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
        UP_ARM_SHAPE = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
        DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
        SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
        NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
        EAST_ARM_SHAPE = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
        SHORT_UP_ARM_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
        SHORT_DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
        SHORT_SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
        SHORT_NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
        SHORT_EAST_ARM_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        SHORT_WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
        SHORT_HEAD_SHAPES = getHeadShapes(true);
        HEAD_SHAPES = getHeadShapes(false);
    }
}
