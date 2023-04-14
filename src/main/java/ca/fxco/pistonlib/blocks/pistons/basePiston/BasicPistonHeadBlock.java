package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.Arrays;

import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import lombok.Getter;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Getter
public class BasicPistonHeadBlock extends DirectionalBlock {

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

    private final PistonFamily family;

    public static VoxelShape[] getHeadShapes(boolean shortHead) {
        return Arrays.stream(Direction.values()).map((dir) -> getHeadShape(dir, shortHead)).toArray(VoxelShape[]::new);
    }

    //TODO: Make PistonHeadBlock.getHeadShape() public and call it in here instead of re-initializing all this garbage
    public static VoxelShape getHeadShape(Direction direction, boolean shortHead) {
        return switch (direction) {
            default -> Shapes.or(DOWN_HEAD_SHAPE, shortHead ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE);
            case UP -> Shapes.or(UP_HEAD_SHAPE, shortHead ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE);
            case NORTH -> Shapes.or(NORTH_HEAD_SHAPE, shortHead ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE);
            case SOUTH -> Shapes.or(SOUTH_HEAD_SHAPE, shortHead ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE);
            case WEST -> Shapes.or(WEST_HEAD_SHAPE, shortHead ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE);
            case EAST -> Shapes.or(EAST_HEAD_SHAPE, shortHead ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE);
        };
    }

    public BasicPistonHeadBlock(PistonFamily family) {
        this(family, FabricBlockSettings.copyOf(Blocks.PISTON_HEAD));
    }

    public BasicPistonHeadBlock(PistonFamily family, Properties properties) {
        super(properties);

        this.family = family;
        this.family.setHead(this);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(SHORT, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(SHORT) ? SHORT_HEAD_SHAPES : HEAD_SHAPES)[state.getValue(FACING).ordinal()];
    }

    public boolean isFittingBase(BlockState headState, BlockState behindState) {
        return behindState.is(family.getBase(headState.getValue(TYPE))) && behindState.getValue(BasicPistonBaseBlock.EXTENDED) &&
                behindState.getValue(FACING) == headState.getValue(FACING);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild) {
            BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());

            if (this.isFittingBase(state, level.getBlockState(behindPos))) {
                level.destroyBlock(behindPos, false);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(this)) {
            super.onRemove(state, level, pos, newState, movedByPiston);

            if (!movedByPiston) {
                Direction facing = state.getValue(FACING);
                BlockPos behindPos = pos.relative(facing.getOpposite());

                if (this.isFittingBase(state, level.getBlockState(behindPos)) &&
                    !(newState.is(this.family.getArm()) && newState.getValue(FACING) == facing)) {
                    level.destroyBlock(behindPos, true);
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                                LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState behindState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        return this.isFittingBase(state, behindState) || behindState.is(this.family.getMoving()) &&
                behindState.getValue(FACING) == state.getValue(FACING);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (state.canSurvive(level, pos)) {
            BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());
            level.neighborChanged(level.getBlockState(behindPos), behindPos, neighborBlock, neighborPos, false);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(family.getBase(state.getValue(TYPE)));
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
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    static {

        TYPE = BlockStateProperties.PISTON_TYPE;
        SHORT = BlockStateProperties.SHORT;
        EAST_HEAD_SHAPE = Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        WEST_HEAD_SHAPE = Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
        SOUTH_HEAD_SHAPE = Block.box(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
        NORTH_HEAD_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
        UP_HEAD_SHAPE = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
        DOWN_HEAD_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
        UP_ARM_SHAPE = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
        DOWN_ARM_SHAPE = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
        SOUTH_ARM_SHAPE = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
        NORTH_ARM_SHAPE = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
        EAST_ARM_SHAPE = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        WEST_ARM_SHAPE = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
        SHORT_UP_ARM_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
        SHORT_DOWN_ARM_SHAPE = Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
        SHORT_SOUTH_ARM_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
        SHORT_NORTH_ARM_SHAPE = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
        SHORT_EAST_ARM_SHAPE = Block.box(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
        SHORT_WEST_ARM_SHAPE = Block.box(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
        SHORT_HEAD_SHAPES = getHeadShapes(true);
        HEAD_SHAPES = getHeadShapes(false);

    }
}
