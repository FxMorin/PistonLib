package ca.fxco.pistonlib.blocks.pistons.basePiston;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.api.pistonlib.pistonLogic.MotionType;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.MergingPistonStructureResolver;
import ca.fxco.pistonlib.pistonLogic.structureRunners.BasicStructureRunner;
import ca.fxco.pistonlib.pistonLogic.structureRunners.MergingStructureRunner;
import ca.fxco.api.pistonlib.pistonLogic.structure.StructureRunner;
import lombok.Getter;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Getter
public class BasicPistonBaseBlock extends DirectionalBlock {

	public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);

    private final PistonFamily family;
    private final PistonType type;

    public BasicPistonBaseBlock(PistonFamily family, PistonType type) {
        this(family, type, FabricBlockSettings.copyOf(Blocks.PISTON));
    }

    public BasicPistonBaseBlock(PistonFamily family, PistonType type, Properties properties) {
        super(properties);

        this.family = family;
        this.type = type;
        this.family.setBase(this);

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(EXTENDED, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (state.getValue(EXTENDED)) {
            return switch (state.getValue(FACING)) {
                case UP    -> EXTENDED_UP_SHAPE;
                case DOWN  -> EXTENDED_DOWN_SHAPE;
                case NORTH -> EXTENDED_NORTH_SHAPE;
                case SOUTH -> EXTENDED_SOUTH_SHAPE;
                case EAST  -> EXTENDED_EAST_SHAPE;
                case WEST  -> EXTENDED_WEST_SHAPE;
            };
        }

        return Shapes.block();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        this.checkIfExtend(level, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        this.checkIfExtend(level, pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && level.getBlockEntity(pos) == null) {
            this.checkIfExtend(level, pos, state);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
            .setValue(FACING, ctx.getNearestLookingDirection().getOpposite())
            .setValue(EXTENDED, false);
    }

    public BasicStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, int length, boolean extend) {
        return PistonLibConfig.mergingApi ?
                new MergingPistonStructureResolver(this, level, pos, facing, length, extend) :
                new BasicStructureResolver(this, level, pos, facing, length, extend);
    }

    public StructureRunner newStructureRunner(Level level, BlockPos pos, Direction facing, int length, boolean extend,
                                              BasicStructureResolver.Factory<? extends BasicStructureResolver> structureProvider) {
        return PistonLibConfig.mergingApi ?
                new MergingStructureRunner(level, pos, facing, length, this.family, this.type, extend , structureProvider) :
                new BasicStructureRunner(level, pos, facing, length, this.family, this.type, extend , structureProvider);
    }

    public void checkIfExtend(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        Direction facing = state.getValue(FACING);
        int length = this.getLength(level, pos, state);
        boolean shouldExtend = this.hasNeighborSignal(level, pos, facing);

        if (PistonLibConfig.headlessPistonFix && length > this.family.getMinLength()) {
            BlockState blockState = level.getBlockState(pos.relative(facing, length));
            if (shouldExtend && !blockState.is(this.family.getMoving()) && !blockState.is(this.family.getHead())) {
                level.removeBlock(pos, false);
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX(), pos.getY(), pos.getZ(),
                        new ItemStack(this.family.getBase(this.type).asItem())
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
                return;
            }
        }

        if (shouldExtend && length < this.family.getMaxLength()) {
            if (this.newStructureResolver(level, pos, facing, length, true).resolve()) {
                level.blockEvent(pos, this, MotionType.PUSH, facing.get3DDataValue());
            }
        } else if (!shouldExtend && length > this.family.getMinLength()) {
            int type = getPullType((ServerLevel)level, pos, facing, length);
            if (type != MotionType.NONE) {
                level.blockEvent(pos, this, type, facing.get3DDataValue());
            }
        }
    }

    protected int getLength(Level level, BlockPos pos, BlockState state) {
        return state.getValue(EXTENDED) ? this.family.getMaxLength() : this.family.getMinLength();
    }

    protected int getPullType(ServerLevel level, BlockPos pos, Direction facing, int length) {
        // make sure the piston doesn't try to retract while it's already retracting
        BlockPos headPos = pos.relative(facing, length);
        BlockState headState = level.getBlockState(headPos);

        if (headState.is(this.family.getMoving())) {
            if (level.getBlockEntity(headPos) instanceof PistonMovingBlockEntity mbe &&
                mbe.isSourcePiston() && !mbe.isExtending() && mbe.getDirection() == facing) {
                return MotionType.NONE;
            }
        }

        BlockPos frontPos = pos.relative(facing, length + 1);
        BlockState frontState = level.getBlockState(frontPos);

        if (frontState.is(this.family.getMoving()) && frontState.getValue(FACING) == facing) {
            if (level.getBlockEntity(frontPos) instanceof PistonMovingBlockEntity mbe &&
                    mbe.isExtending() &&
                    (mbe.getProgress(0.0F) < 0.5F ||
                            mbe.getLastTicked() == level.getGameTime() || level.isHandlingTick())) {
                return MotionType.RETRACT;
            }
        }

        return MotionType.PULL;
    }

    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) ||
                level.pl$hasQuasiNeighborSignal(pos, 1);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
        Direction facing = state.getValue(FACING);

        if (!level.isClientSide()) {
            boolean shouldExtend = this.hasNeighborSignal(level, pos, facing);

            if (shouldExtend && MotionType.isRetract(type)) {
                level.setBlock(pos, state.setValue(EXTENDED, true), UPDATE_CLIENTS);
                return false;
            }
            if (!shouldExtend && MotionType.isExtend(type)) {
                return false;
            }
        }

        int length = this.getLength(level, pos, state);

        if (MotionType.isExtend(type)) {
            if (!this.moveBlocks(level, pos, facing, length, true)) {
                return false;
            }

            if (length > 0) {
                BlockPos armPos = pos.relative(facing, length);
                BlockState armState = this.family.getArm().defaultBlockState().
                    setValue(BasicPistonArmBlock.FACING, facing).
                    setValue(BasicPistonArmBlock.SHORT, false);

                level.setBlock(armPos, armState, UPDATE_MOVE_BY_PISTON | UPDATE_ALL);
            } else {
                level.setBlock(pos, state.setValue(EXTENDED, true), UPDATE_MOVE_BY_PISTON | UPDATE_ALL);
            }

            playEvents(level, GameEvent.PISTON_EXTEND, pos);
        } else if (MotionType.isRetract(type)) {
            BlockPos headPos = pos.relative(facing, length);
            BlockEntity headBlockEntity = level.getBlockEntity(headPos);

            if (headBlockEntity instanceof BasicMovingBlockEntity mbe) {
                mbe.finalTick();
            }

            int newLength = length - 1;
            BlockPos sourcePos = pos.relative(facing, newLength);
            BlockState sourceState = (newLength > 0)
                ? this.family.getHead().defaultBlockState()
                    .setValue(BasicPistonHeadBlock.FACING, Direction.from3DDataValue(data & 7))
                    .setValue(BasicPistonHeadBlock.TYPE, this.type)
                : this.defaultBlockState()
                    .setValue(FACING, Direction.from3DDataValue(data & 7));

            BlockState movingBaseState = this.family.getMoving().defaultBlockState()
                .setValue(MovingPistonBlock.FACING, facing)
                .setValue(MovingPistonBlock.TYPE, this.type);
            BlockEntity movingBaseBlockEntity = this.family.newMovingBlockEntity(
                sourcePos,
                movingBaseState,
                sourceState,
                null,
                facing,
                false,
                true
            );
            level.setBlock(sourcePos, movingBaseState, UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBaseBlockEntity);

            level.updateNeighborsAt(sourcePos, movingBaseState.getBlock());
            movingBaseState.updateNeighbourShapes(level, sourcePos, UPDATE_CLIENTS);

            if (this.type == PistonType.STICKY) {
                boolean droppedBlock = false;

                BlockPos frontPos = pos.relative(facing, length + 1);
                BlockState frontState = level.getBlockState(frontPos);

                if (frontState.is(this.family.getMoving())) {
                    BlockEntity frontBlockEntity = level.getBlockEntity(frontPos);

                    if (frontBlockEntity instanceof PistonMovingBlockEntity mbe && mbe.getDirection() == facing && mbe.isExtending()) {
                        mbe.finalTick();
                        droppedBlock = true;
                    }
                }
                if (!droppedBlock) {
                    if (type != MotionType.PULL || frontState.isAir() ||
                            (frontState.getPistonPushReaction() != PushReaction.NORMAL && !frontState.is(ModTags.PISTONS)) ||
                            !canMoveBlock(frontState, level, frontPos, facing.getOpposite(), false, facing)) {
                        if (!PistonLibConfig.illegalBreakingFix ||
                                level.getBlockState(headPos).getDestroySpeed(level, headPos) != -1.0F) {
                            level.removeBlock(headPos, false);
                        }
                    } else {
                        this.moveBlocks(level, pos, facing, length, false);
                    }
                }
            } else {
                if (!PistonLibConfig.illegalBreakingFix ||
                        level.getBlockState(headPos).getDestroySpeed(level, headPos) != -1.0F) {
                    level.removeBlock(headPos, false);
                }
            }

            playEvents(level, GameEvent.PISTON_CONTRACT, pos);
        }

        return true;
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
        builder.add(FACING, EXTENDED);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(EXTENDED);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    public void playEvents(Level level, GameEvent event, BlockPos pos) {
        level.playSound(
            null,
            pos,
            event == GameEvent.PISTON_CONTRACT ?
                SoundEvents.PISTON_CONTRACT : SoundEvents.PISTON_EXTEND,
            SoundSource.BLOCKS,
            0.5F,
            0.6F + 0.25F * level.getRandom().nextFloat()
        );
        level.gameEvent(null, event, pos);
    }

    /**
     * Returns whether this piston can move the given block,
     * taking into account world height/border as well as
     * vanilla and custom piston push reactions.
     */
    public boolean canMoveBlock(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
        // coordinate related checks (world height/world border)

        if (level.isOutsideBuildHeight(pos) || !(PistonLibConfig.pushThroughWorldBorderFix ? level.getWorldBorder().isWithinBounds(pos.relative(moveDir)) : level.getWorldBorder().isWithinBounds(pos))) {
            return false;
        } else if (state.isAir()) {
            return true; // air is never in the way
        } else if (moveDir == Direction.DOWN) {
            if (pos.getY() == level.getMinBuildHeight()) {
                return false;
            }
        } else if (moveDir == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1) {
            return false;
        }


        // piston push reaction/ custom piston behavior

        if (state.pl$usesConfigurablePistonBehavior()) { // This is where stuff gets fun
            if (!state.pl$isMovable(level, pos)) {
                return false;
            } else if (moveDir == pistonFacing) {
                if (!state.pl$canPistonPush(level, pos, moveDir)) {
                    return false;
                }
            } else {
                if (!state.pl$canPistonPull(level, pos, moveDir)) {
                    return false;
                }
            }
            if (state.pl$canDestroy(level, pos) && !allowDestroy) {
                return false;
            }
        } else {
            if (state.is(ModTags.UNPUSHABLE)) {
                return false;
            } else if (state.is(ModTags.PISTONS)) {
                if (state.getValue(EXTENDED)) {
                    return false;
                }
            } else { // Pistons shouldn't be checked against destroy speed or PistonPushReaction
                if (state.getDestroySpeed(level, pos) == -1.0F) {
                    return false;
                }
                switch (state.getPistonPushReaction()) {
                    case BLOCK -> { return false; }
                    case DESTROY -> {
                        if (!allowDestroy) {
                            return false;
                        }
                    }
                    case PUSH_ONLY -> {
                        if (moveDir != pistonFacing) {
                            return false;
                        }
                    }
                    default -> { }
                }
            }
        }

        // custom piston behavior
        return canMoveBlock(state);
    }

    /**
     * Returns whether this piston can move the given block.
     * This methods assumes the world height/border and push
     * reaction checks have all succeeded.
     */
    public boolean canMoveBlock(BlockState state) {
        return !state.hasBlockEntity();
    }

    public boolean moveBlocks(Level level, BlockPos pos, Direction facing, int length, boolean extend) {
        StructureRunner structureRunner = newStructureRunner(level, pos, facing, length, extend, this::newStructureResolver);
        return structureRunner.run();
    }
}
