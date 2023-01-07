package ca.fxco.pistonlib.blocks.pistons.longPiston;

import java.util.Arrays;
import java.util.function.BiPredicate;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

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
import net.minecraft.world.phys.shapes.VoxelShape;

public class LongPistonArmBlock extends DirectionalBlock {

    // This is the BASIC ARM BLOCK that you should be extending to create your own arm blocks

    public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
    public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;

    // TODO: Grab there directly instead of using a lookup since it should be faster now that there is no union
    protected static final VoxelShape UP_ARM_SHAPE = Block.box(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape DOWN_ARM_SHAPE = Block.box(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
    protected static final VoxelShape SOUTH_ARM_SHAPE = Block.box(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape NORTH_ARM_SHAPE = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
    protected static final VoxelShape EAST_ARM_SHAPE = Block.box(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape WEST_ARM_SHAPE = Block.box(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_UP_ARM_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape SHORT_DOWN_ARM_SHAPE = Block.box(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape SHORT_SOUTH_ARM_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape SHORT_NORTH_ARM_SHAPE = Block.box(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape SHORT_EAST_ARM_SHAPE = Block.box(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_WEST_ARM_SHAPE = Block.box(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape[] SHORT_ARM_SHAPES = getArmShapes(true);
    private static final VoxelShape[] ARM_SHAPES = getArmShapes(false);

    BiPredicate<BlockState,BlockState> OR_IS_ATTACHED = (state, selfState) ->
            state.is(ModTags.MOVING_PISTONS) && state.getValue(FACING) == selfState.getValue(FACING);

    private LongPistonHeadBlock HEAD_BLOCK;

    public static VoxelShape getArmShape(Direction direction, boolean shortArm) {
        return switch (direction) {
            default -> shortArm ? SHORT_DOWN_ARM_SHAPE : DOWN_ARM_SHAPE;
            case UP -> shortArm ? SHORT_UP_ARM_SHAPE : UP_ARM_SHAPE;
            case NORTH -> shortArm ? SHORT_NORTH_ARM_SHAPE : NORTH_ARM_SHAPE;
            case SOUTH -> shortArm ? SHORT_SOUTH_ARM_SHAPE : SOUTH_ARM_SHAPE;
            case WEST -> shortArm ? SHORT_WEST_ARM_SHAPE : WEST_ARM_SHAPE;
            case EAST -> shortArm ? SHORT_EAST_ARM_SHAPE : EAST_ARM_SHAPE;
        };
    }

    public static VoxelShape[] getArmShapes(boolean shortArm) {
        return Arrays.stream(Direction.values()).map((dir) -> getArmShape(dir, shortArm)).toArray(VoxelShape[]::new);
    }

    public LongPistonArmBlock() {
        this(FabricBlockSettings.copyOf(Blocks.PISTON_HEAD));
    }

    public LongPistonArmBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(TYPE, PistonType.DEFAULT)
            .setValue(SHORT, false));
    }

    public LongPistonArmBlock(Properties properties, LongPistonHeadBlock headBlock) {
        super(properties);

        HEAD_BLOCK = headBlock;

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(TYPE, PistonType.DEFAULT)
            .setValue(SHORT, false));
    }

    public LongPistonHeadBlock getHeadBlock() {
        return HEAD_BLOCK;
    }

    public void setHeadBlock(LongPistonHeadBlock headBlock) {
        HEAD_BLOCK = headBlock;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return (state.getValue(SHORT) ? SHORT_ARM_SHAPES : ARM_SHAPES)[state.getValue(FACING).ordinal()];
    }

    public boolean isAttached(BlockState state, BlockState behindState, BlockState frontState) {
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        if (!frontState.is(HEAD_BLOCK) && !frontState.is(this)) {
            return false;
        }

        PistonType type = state.getValue(TYPE);
        Direction facing = state.getValue(FACING);

        // Head or Arm in front of arm is valid
        if (type != frontState.getValue(TYPE) || facing != frontState.getValue(FACING)) {
            return false;
        }

        // If arm is behind, make sure it's a valid arm
        if (behindState.is(this)) {
            return type == behindState.getValue(TYPE) && facing == behindState.getValue(FACING);
        }

        // If it's not an arm than it must be a piston base, and a valid one
        PistonFamily family = PistonFamilies.getFamily(HEAD_BLOCK);
        Block base = family.getBaseBlock(type);

        return behindState.is(base) && behindState.getValue(BasicPistonBaseBlock.EXTENDED) &&
            facing == behindState.getValue(FACING);
    }

    public void isAttachedOrBreak(Level level, BlockState state, BlockPos behindPos, BlockPos frontPos) {
        boolean validFront, validBack;
        BlockState frontState = level.getBlockState(frontPos);
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        validFront = (frontState.is(HEAD_BLOCK) || frontState.is(this)) &&
                (state.getValue(TYPE) == frontState.getValue(TYPE) && state.getValue(FACING) == frontState.getValue(FACING));
        BlockState backState = level.getBlockState(behindPos);
        if (backState.is(this)) { // If arm is behind, make sure it's a valid arm
            validBack = state.getValue(TYPE) == backState.getValue(TYPE) && backState.getValue(FACING) == state.getValue(FACING);
        } else { // If it's not an arm than it must be a piston base, and a valid one
            PistonFamily f = PistonFamilies.getFamily(HEAD_BLOCK);
            Block piston = f.getBaseBlock(state.getValue(TYPE));
            validBack = backState.is(piston) && backState.getValue(BasicPistonBaseBlock.EXTENDED) &&
                    backState.getValue(FACING) == backState.getValue(FACING);
        }
        if (!validBack || !validFront) {
            if (validBack) level.destroyBlock(behindPos, false);
            if (validFront) level.destroyBlock(frontPos, false);
        }
        //world.breakBlock(backPos, false);
        //world.breakBlock(frontPos, false);
    }

    public boolean isAttachedOrBreak(
            Level world,
            BlockState armState,
            BlockPos backPos,
            BlockPos frontPos,
            BiPredicate<BlockState, BlockState> backOr,
            BiPredicate<BlockState, BlockState> frontOr
    ) {
        BlockState frontState = world.getBlockState(frontPos);
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        if (frontOr.test(frontState, armState) || ((frontState.is(HEAD_BLOCK) || frontState.is(this)) &&
                (armState.getValue(TYPE) == frontState.getValue(TYPE) && armState.getValue(FACING) == frontState.getValue(FACING)))) {
            BlockState backState = world.getBlockState(backPos);
            if (backOr.test(backState, armState)) {
                return true;
            } else if (backState.is(this)) { // If arm is behind, make sure it's a valid arm
                return armState.getValue(TYPE) == backState.getValue(TYPE) && backState.getValue(FACING) == armState.getValue(FACING);
            } else { // If it's not an arm than it must be a piston base, and a valid one
                PistonFamily f = PistonFamilies.getFamily(HEAD_BLOCK);
                Block piston = f.getBaseBlock(armState.getValue(TYPE));
                return backState.is(piston) && backState.getValue(BasicPistonBaseBlock.EXTENDED) &&
                        backState.getValue(FACING) == backState.getValue(FACING);
            }
        }
        return false;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild) {
            Direction facing = state.getValue(FACING);
            this.isAttachedOrBreak(level, state, pos.relative(facing.getOpposite()), pos.relative(facing));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.is(this)) {
            super.onRemove(state, level, pos, newState, moved);
            Direction facing = state.getValue(FACING);
            this.isAttachedOrBreak(level, state, pos.relative(facing.getOpposite()), pos.relative(facing));
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
        Direction dir = state.getValue(FACING);
        return this.isAttachedOrBreak((Level)level, state, pos.relative(dir.getOpposite()), pos.relative(dir),
                OR_IS_ATTACHED, OR_IS_ATTACHED);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (state.canSurvive(level, pos)) {
            Direction dir = state.getValue(FACING);
            BlockPos backPos = pos.relative(dir.getOpposite());
            BlockPos frontPos = pos.relative(dir);
            level.neighborChanged(level.getBlockState(backPos), backPos, neighborBlock, neighborPos, false);
            level.neighborChanged(level.getBlockState(frontPos), frontPos, neighborBlock, neighborPos, false);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        PistonFamily family = PistonFamilies.getFamily(HEAD_BLOCK);
        return new ItemStack(family.getBaseBlock(state.getValue(TYPE)));
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
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}
