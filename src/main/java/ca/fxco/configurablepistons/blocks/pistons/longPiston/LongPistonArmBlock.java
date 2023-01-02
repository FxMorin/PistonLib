package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Arrays;
import java.util.function.BiPredicate;

public class LongPistonArmBlock extends FacingBlock {

    // This is the BASIC ARM BLOCK that you should be extending to create your own arm blocks

    public static final BooleanProperty SHORT = Properties.SHORT;
    public static final EnumProperty<PistonType> TYPE = Properties.PISTON_TYPE;

    // TODO: Grab there directly instead of using a lookup since it should be faster now that there is no union
    protected static final VoxelShape UP_ARM_SHAPE = Block.createCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
    protected static final VoxelShape SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
    protected static final VoxelShape EAST_ARM_SHAPE = Block.createCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_UP_ARM_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape SHORT_DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape SHORT_SOUTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape SHORT_NORTH_ARM_SHAPE = Block.createCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape SHORT_EAST_ARM_SHAPE = Block.createCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_WEST_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape[] SHORT_ARM_SHAPES = getArmShapes(true);
    private static final VoxelShape[] ARM_SHAPES = getArmShapes(false);

    BiPredicate<BlockState,BlockState> OR_IS_ATTACHED = (state, selfState) ->
            state.isIn(ModTags.MOVING_PISTONS) && state.get(FACING) == selfState.get(FACING);

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

    public LongPistonArmBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, PistonType.DEFAULT)
                .with(SHORT, false));
    }

    public LongPistonArmBlock(Settings settings, LongPistonHeadBlock headBlock) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, PistonType.DEFAULT)
                .with(SHORT, false));
        HEAD_BLOCK = headBlock;
    }

    public LongPistonHeadBlock getHeadBlock() {
        return HEAD_BLOCK;
    }

    public void setHeadBlock(LongPistonHeadBlock headBlock) {
        HEAD_BLOCK = headBlock;
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (state.get(SHORT) ? SHORT_ARM_SHAPES : ARM_SHAPES)[state.get(FACING).ordinal()];
    }

    public boolean isAttached(BlockState armState, BlockState backState, BlockState frontState) {
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        if (!frontState.isOf(HEAD_BLOCK) && !frontState.isOf(this)) return false;
        PistonType armType = armState.get(TYPE);
        Direction armFacing = armState.get(FACING);
        // Head or Arm in front of arm is valid
        if (armType != frontState.get(TYPE) || armFacing != frontState.get(FACING)) return false;
        // If arm is behind, make sure it's a valid arm
        if (backState.isOf(this)) return armType == backState.get(TYPE) && backState.get(FACING) == armFacing;
        // If it's not an arm than it must be a piston base, and a valid one
        PistonFamily family = PistonFamilies.getFamily(HEAD_BLOCK);
        Block piston = armType == PistonType.DEFAULT ? family.getPistonBlock() : family.getStickyPistonBlock();
        return backState.isOf(piston) && backState.get(BasicPistonBlock.EXTENDED) &&
                backState.get(FACING) == armFacing;
    }

    public void isAttachedOrBreak(World world, BlockState armState, BlockPos backPos, BlockPos frontPos) {
        boolean validFront, validBack;
        BlockState frontState = world.getBlockState(frontPos);
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        validFront = (frontState.isOf(HEAD_BLOCK) || frontState.isOf(this)) &&
                (armState.get(TYPE) == frontState.get(TYPE) && armState.get(FACING) == frontState.get(FACING));
        BlockState backState = world.getBlockState(backPos);
        if (backState.isOf(this)) { // If arm is behind, make sure it's a valid arm
            validBack = armState.get(TYPE) == backState.get(TYPE) && backState.get(FACING) == armState.get(FACING);
        } else { // If it's not an arm than it must be a piston base, and a valid one
            PistonFamily f = PistonFamilies.getFamily(HEAD_BLOCK);
            Block piston = armState.get(TYPE) == PistonType.DEFAULT ? f.getPistonBlock() : f.getStickyPistonBlock();
            validBack = backState.isOf(piston) && backState.get(BasicPistonBlock.EXTENDED) &&
                    backState.get(FACING) == backState.get(FACING);
        }
        if (!validBack || !validFront) {
            if (validBack) world.breakBlock(backPos, false);
            if (validFront) world.breakBlock(frontPos, false);
        }
        //world.breakBlock(backPos, false);
        //world.breakBlock(frontPos, false);
    }

    public boolean isAttachedOr(
            World world,
            BlockState armState,
            BlockPos backPos,
            BlockPos frontPos,
            BiPredicate<BlockState, BlockState> backOr,
            BiPredicate<BlockState, BlockState> frontOr
    ) {
        BlockState frontState = world.getBlockState(frontPos);
        // Must be BasicPistonArmBlock or BasicPistonHeadBlock in front
        if (frontOr.test(frontState, armState) || ((frontState.isOf(HEAD_BLOCK) || frontState.isOf(this)) &&
                (armState.get(TYPE) == frontState.get(TYPE) && armState.get(FACING) == frontState.get(FACING)))) {
            BlockState backState = world.getBlockState(backPos);
            if (backOr.test(backState, armState)) {
                return true;
            } else if (backState.isOf(this)) { // If arm is behind, make sure it's a valid arm
                return armState.get(TYPE) == backState.get(TYPE) && backState.get(FACING) == armState.get(FACING);
            } else { // If it's not an arm than it must be a piston base, and a valid one
                PistonFamily f = PistonFamilies.getFamily(HEAD_BLOCK);
                Block piston = armState.get(TYPE) == PistonType.DEFAULT ? f.getPistonBlock() : f.getStickyPistonBlock();
                return backState.isOf(piston) && backState.get(BasicPistonBlock.EXTENDED) &&
                        backState.get(FACING) == backState.get(FACING);
            }
        }
        return false;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.getAbilities().creativeMode) {
            Direction dir = state.get(FACING);
            this.isAttachedOrBreak(world, state, pos.offset(dir.getOpposite()), pos.offset(dir));
        }
        super.onBreak(world, pos, state, player);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;
        super.onStateReplaced(state, world, pos, newState, moved);
        Direction dir = state.get(FACING);
        this.isAttachedOrBreak(world, state, pos.offset(dir.getOpposite()), pos.offset(dir));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ?
                Blocks.AIR.getDefaultState() :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction dir = state.get(FACING);
        return this.isAttachedOr((World)world, state, pos.offset(dir.getOpposite()), pos.offset(dir),
                OR_IS_ATTACHED, OR_IS_ATTACHED);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean n) {
        if (state.canPlaceAt(world, pos)) {
            Direction dir = state.get(FACING);
            BlockPos backPos = pos.offset(dir.getOpposite());
            BlockPos frontPos = pos.offset(dir);
            world.getBlockState(backPos).neighborUpdate(world, backPos, block, fromPos, false);
            world.getBlockState(frontPos).neighborUpdate(world, frontPos, block, fromPos, false);
        }
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        PistonFamily family = PistonFamilies.getFamily(HEAD_BLOCK);
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
}
