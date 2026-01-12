package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.Arrays;

import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilyMember;
import com.mojang.serialization.MapCodec;
import lombok.Getter;

import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static ca.fxco.pistonlib.PistonLib.DIRECTIONS;

@Getter
public class BasicPistonHeadBlock extends DirectionalBlock implements PistonFamilyMember {

    public static final EnumProperty<PistonType> TYPE;
    public static final BooleanProperty SHORT;
    private static final VoxelShape[] SHORT_HEAD_SHAPES;
    private static final VoxelShape[] HEAD_SHAPES;

    private PistonFamily family;

    public BasicPistonHeadBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(SHORT, false));
    }

    @Override
    public void setFamily(PistonFamily family) {
        if (this.family != null) {
            throw new IllegalStateException("Family has already been set! - " + this.family);
        }
        this.family = family;
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
        return behindState.is(family.getBase(headState.getValue(TYPE))) &&
                behindState.getValue(BlockStateProperties.EXTENDED) &&
                behindState.getValue(FACING) == headState.getValue(FACING);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild) {
            BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());

            if (this.isFittingBase(state, level.getBlockState(behindPos))) {
                level.destroyBlock(behindPos, false);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
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
    public BlockState updateShape(BlockState state, LevelReader levelReader,
                                  ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction dir,
                                  BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        return dir.getOpposite() == state.getValue(FACING) && !state.canSurvive(levelReader, pos) ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(state, levelReader, scheduledTickAccess,
                        pos, dir, neighborPos, neighborState, randomSource);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState behindState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
        return this.isFittingBase(state, behindState) || behindState.is(this.family.getMoving()) &&
                behindState.getValue(FACING) == state.getValue(FACING);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (state.canSurvive(level, pos)) {
            BlockPos behindPos = pos.relative(state.getValue(FACING).getOpposite());
            level.neighborChanged(level.getBlockState(behindPos), behindPos, neighborBlock, orientation, false);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean bl) {
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
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return null;
    }

    public static VoxelShape[] getHeadShapes(boolean shortHead) {
        return Arrays.stream(DIRECTIONS).map((dir) -> getHeadShape(dir, shortHead)).toArray(VoxelShape[]::new);
    }

    public static VoxelShape getHeadShape(Direction direction, boolean shortHead) {
        return PistonHeadBlock.calculateShape(direction, shortHead);
    }

    static {
        TYPE = BlockStateProperties.PISTON_TYPE;
        SHORT = BlockStateProperties.SHORT;
        SHORT_HEAD_SHAPES = getHeadShapes(true);
        HEAD_SHAPES = getHeadShapes(false);
    }
}
