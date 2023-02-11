package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.blocks.pistons.mergePiston.MergeBlockEntity;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.pistonlib.impl.QLevel;
import ca.fxco.pistonlib.pistonLogic.MotionType;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.piston.PistonStructureResolver;
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

public class BasicPistonBaseBlock extends DirectionalBlock {

	public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);

    public final PistonFamily family;
    public final PistonType type;

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

    public BasicStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new BasicStructureResolver(this, level, pos, facing, extend);
    }

    public void checkIfExtend(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        Direction facing = state.getValue(FACING);
        boolean isExtended = state.getValue(EXTENDED);
        boolean shouldBeExtended = this.hasNeighborSignal(level, pos, facing);

        if (shouldBeExtended && !isExtended) {
            if (this.newStructureResolver(level, pos, facing, true).resolve()) {
                level.blockEvent(pos, this, MotionType.PUSH, facing.get3DDataValue());
            }
        } else if (!shouldBeExtended && isExtended) {
            int type = getPullType((ServerLevel)level, pos, facing);
            if (type != MotionType.NONE) {
                level.blockEvent(pos, this, type, facing.get3DDataValue());
            }
        }
    }

    protected int getPullType(ServerLevel level, BlockPos pos, Direction facing) {
        BlockPos frontPos = pos.relative(facing, 2);
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
                ((QLevel)level).hasQuasiNeighborSignal(pos, 1);
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
        if (MotionType.isExtend(type)) {
            if (!this.moveBlocks(level, pos, facing, true)) {
                return false;
            }

            level.setBlock(pos, state.setValue(EXTENDED, true), UPDATE_MOVE_BY_PISTON | UPDATE_ALL);
            playEvents(level, GameEvent.PISTON_EXTEND, pos);
        } else if (MotionType.isRetract(type)) {
            BlockPos headPos = pos.relative(facing);
            BlockEntity headBlockEntity = level.getBlockEntity(headPos);

            if (headBlockEntity instanceof BasicMovingBlockEntity mbe) {
                mbe.finalTick();
            }

            BlockState movingBaseState = this.family.getMoving().defaultBlockState()
                .setValue(MovingPistonBlock.FACING, facing)
                .setValue(MovingPistonBlock.TYPE, this.type);
            BlockEntity movingBaseBlockEntity = this.family.newMovingBlockEntity(
                pos,
                movingBaseState,
                this.defaultBlockState()
                    .setValue(FACING, Direction.from3DDataValue(data & 7)),
                null,
                facing,
                false,
                true
            );
            level.setBlock(pos, movingBaseState, UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBaseBlockEntity);

            level.updateNeighborsAt(pos, movingBaseState.getBlock());
            movingBaseState.updateNeighbourShapes(level, pos, UPDATE_CLIENTS);

            if (this.type == PistonType.STICKY) {
                boolean droppedBlock = false;

                BlockPos frontPos = pos.relative(facing, 2);
                BlockState frontState = level.getBlockState(frontPos);

                if (frontState.is(this.family.getMoving())) {
                    BlockEntity frontBlockEntity = level.getBlockEntity(frontPos);

                    if (frontBlockEntity instanceof PistonMovingBlockEntity mbe && mbe.getDirection() == facing && mbe.isExtending()) {
                        mbe.finalTick();
                        droppedBlock = true;
                    } else if (frontBlockEntity instanceof MergeBlockEntity mbe) {
                        MergeBlockEntity.MergeData mergeData = mbe.getMergingBlocks().get(facing);
                        mergeData.setProgress(1F);
                        droppedBlock = true;
                    }
                }
                if (!droppedBlock) {
                    if (type != MotionType.PULL || frontState.isAir() ||
                            (frontState.getPistonPushReaction() != PushReaction.NORMAL && !frontState.is(ModTags.PISTONS)) ||
                            !canMoveBlock(frontState, level, frontPos, facing.getOpposite(), false, facing)) {
                        level.removeBlock(headPos, false);
                    } else {
                        this.moveBlocks(level, pos, facing, false);
                    }
                }
            } else {
                level.removeBlock(headPos, false);
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

        if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos))
            return false;
        if (state.isAir())
            return true; // air is never in the way
        if (moveDir == Direction.DOWN && pos.getY() == level.getMinBuildHeight())
            return false;
        if (moveDir == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1)
            return false;


        // piston push reaction/ custom piston behavior

        ConfigurablePistonBehavior customBehavior = (ConfigurablePistonBehavior)state.getBlock();

        if (customBehavior.usesConfigurablePistonBehavior()) { // This is where stuff gets fun
            if (!customBehavior.isMovable(level, pos, state))
                return false;
            if (moveDir == pistonFacing) {
                if (!customBehavior.canPistonPush(level, pos, state, moveDir))
                    return false;
            } else {
                if (!customBehavior.canPistonPull(level, pos, state, moveDir))
                    return false;
            }
            if (customBehavior.canDestroy(level, pos, state) && !allowDestroy)
                return false;
        } else {
            if (state.is(ModTags.UNPUSHABLE))
                return false;
            if (state.is(ModTags.PISTONS)) {
                if (state.getValue(EXTENDED)) {
                    return false;
                }
            } else { // Pistons shouldn't be checked against destroy speed or PistonPushReaction
                if (state.getDestroySpeed(level, pos) == -1.0F)
                    return false;
                switch (state.getPistonPushReaction()) {
                    case BLOCK -> { return false; }
                    case DESTROY -> {
                        if (!allowDestroy)
                            return false;
                    }
                    case PUSH_ONLY -> {
                        if (moveDir != pistonFacing)
                            return false;
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

    public boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extend) {
        return moveBlocks(level, pos, facing, extend, this::newStructureResolver);
    }

    public boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extend,
                              BasicStructureResolver.Factory<? extends BasicStructureResolver> structureProvider
    ) {
        if (!extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = level.getBlockState(headPos);

            if (headState.is(this.family.getHead())) {
                level.setBlock(headPos, Blocks.AIR.defaultBlockState(),
                    UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            }
        }

        PistonStructureResolver structure = structureProvider.create(level, pos, facing, extend);

        if (!structure.resolve()) {
            return false;
        }

        Map<BlockPos, BlockState> toRemove = new LinkedHashMap<>();
        List<BlockPos> toMove = structure.getToPush();
        List<BlockPos> toDestroy = structure.getToDestroy();
        List<BlockState> statesToMove = new ArrayList<>();
        List<BlockEntity> blockEntitiesToMove = new ArrayList<>();

        // collect blocks to move
        for (BlockPos posToMove : toMove) {
            BlockState stateToMove = level.getBlockState(posToMove);
            BlockEntity blockEntityToMove = level.getBlockEntity(posToMove);

            if (blockEntityToMove != null) {
                level.removeBlockEntity(posToMove);
                blockEntityToMove.setChanged();
            }

            statesToMove.add(stateToMove);
            blockEntitiesToMove.add(blockEntityToMove);
            toRemove.put(posToMove, stateToMove);
        }

        BlockState[] affectedStates = new BlockState[toMove.size() + toDestroy.size()];
        int affectedIndex = 0;

        // destroy blocks
        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos posToDestroy = toDestroy.get(i);
            BlockState stateToDestroy = level.getBlockState(posToDestroy);
            BlockEntity blockEntityToDestroy = level.getBlockEntity(posToDestroy);

            dropResources(stateToDestroy, level, posToDestroy, blockEntityToDestroy);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
            if (!stateToDestroy.is(BlockTags.FIRE)) {
                level.addDestroyBlockEffect(posToDestroy, stateToDestroy);
            }

            affectedStates[affectedIndex++] = stateToDestroy;
        }

        Direction moveDir = extend ? facing : facing.getOpposite();

        // move blocks
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos posToMove = toMove.get(i);
            BlockPos dstPos = posToMove.relative(moveDir);
            BlockState stateToMove = statesToMove.get(i);
            BlockEntity blockEntityToMove = blockEntitiesToMove.get(i);

            toRemove.remove(dstPos);

            BlockState movingBlock = this.family.getMoving().defaultBlockState()
                .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = this.family
                .newMovingBlockEntity(dstPos, movingBlock, stateToMove, blockEntityToMove, facing, extend, false);

            level.setBlock(dstPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);

            affectedStates[affectedIndex++] = stateToMove;
        }

        // place extending head
        if (extend) {
            BlockPos headPos = pos.relative(facing);
            BlockState headState = this.family.getHead().defaultBlockState()
                .setValue(BasicPistonHeadBlock.TYPE, type)
                .setValue(BasicPistonHeadBlock.FACING, facing);

            toRemove.remove(headPos);

            BlockState movingBlock = this.family.getMoving().defaultBlockState()
                .setValue(BasicMovingBlock.FACING, facing);
            BlockEntity movingBlockEntity = this.family
                .newMovingBlockEntity(headPos, movingBlock, headState, null, facing, extend, true);

            level.setBlock(headPos, movingBlock, UPDATE_MOVE_BY_PISTON | UPDATE_INVISIBLE);
            level.setBlockEntity(movingBlockEntity);
        }

        // remove left over blocks
        BlockState air = Blocks.AIR.defaultBlockState();

        for (BlockPos posToRemove : toRemove.keySet()) {
            level.setBlock(posToRemove, air, UPDATE_MOVE_BY_PISTON | UPDATE_KNOWN_SHAPE | UPDATE_CLIENTS);
        }

        // do neighbor updates
        for (Map.Entry<BlockPos, BlockState> entry : toRemove.entrySet()) {
            BlockPos removedPos = entry.getKey();
            BlockState removedState = entry.getValue();

            removedState.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            air.updateNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
            air.updateIndirectNeighbourShapes(level, removedPos, UPDATE_CLIENTS);
        }

        affectedIndex = 0;

        for (int i = toDestroy.size() - 1; i >= 0; i--) {
            BlockPos destroyedPos = toDestroy.get(i);
            BlockState destroyedState = affectedStates[affectedIndex++];

            destroyedState.updateIndirectNeighbourShapes(level, destroyedPos, UPDATE_CLIENTS);
            level.updateNeighborsAt(destroyedPos, destroyedState.getBlock());
        }
        for (int i = toMove.size() - 1; i >= 0; i--) {
            BlockPos movedPos = toMove.get(i);
            BlockState movedState = affectedStates[affectedIndex++];

            level.updateNeighborsAt(movedPos, movedState.getBlock());
        }
        if (extend) {
            level.updateNeighborsAt(pos.relative(facing), this.family.getHead());
        }

        return true;
    }
}
