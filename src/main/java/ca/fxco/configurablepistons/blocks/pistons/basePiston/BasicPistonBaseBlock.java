package ca.fxco.configurablepistons.blocks.pistons.basePiston;

import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.helpers.Utils;
import ca.fxco.configurablepistons.pistonLogic.MotionType;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    public final boolean isSticky;

    protected BasicMovingBlock MOVING_BLOCK;
    protected BasicPistonHeadBlock HEAD_BLOCK;

    public BasicPistonBaseBlock(boolean isSticky) {
        this(isSticky, ModBlocks.BASIC_MOVING_BLOCK, ModBlocks.BASIC_PISTON_HEAD);
    }

    public BasicPistonBaseBlock(boolean isSticky, Properties properties) {
        this(isSticky, properties, ModBlocks.BASIC_MOVING_BLOCK, ModBlocks.BASIC_PISTON_HEAD);
    }

    public BasicPistonBaseBlock(boolean isSticky, BasicMovingBlock movingBlock, BasicPistonHeadBlock headBlock) {
        this(isSticky, FabricBlockSettings.copyOf(Blocks.PISTON), movingBlock, headBlock);
    }

    public BasicPistonBaseBlock(boolean isSticky, Properties properties, BasicMovingBlock movingBlock, BasicPistonHeadBlock headBlock) {
        super(properties);

        this.isSticky = isSticky;

        MOVING_BLOCK = movingBlock;
        HEAD_BLOCK = headBlock;

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(EXTENDED, false)
        );
    }

    public BasicMovingBlock getMovingBlock() {
        return MOVING_BLOCK;
    }

    public BasicPistonHeadBlock getHeadBlock() {
        return HEAD_BLOCK;
    }

    public void setMovingBlock(BasicMovingBlock movingBlock) {
        MOVING_BLOCK = movingBlock;
    }

    public void setHeadBlock(BasicPistonHeadBlock headBlock) {
        HEAD_BLOCK = headBlock;
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

    public ConfigurablePistonStructureResolver createStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurablePistonStructureResolver(level, pos, facing, extend);
    }

    public void checkIfExtend(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        Direction facing = state.getValue(FACING);
        boolean isExtended = state.getValue(EXTENDED);
        boolean shouldBeExtended = this.hasNeighborSignal(level, pos, facing);

        if (shouldBeExtended && !isExtended) {
            if ((createStructureResolver(level, pos, facing, true)).resolve(false)) {
                level.blockEvent(pos, this, MotionType.PUSH, facing.get3DDataValue());
            }
        } else if (!shouldBeExtended && isExtended) {
            int type = shouldDoPullEvent((ServerLevel)level, pos, facing) ? MotionType.PULL : MotionType.RETRACT;
            level.blockEvent(pos, this, type, facing.get3DDataValue());
        }
    }

    private boolean shouldDoPullEvent(ServerLevel level, BlockPos pos, Direction facing) {
        BlockPos frontPos = pos.relative(facing, 2);
        BlockState frontState = level.getBlockState(frontPos);

        if (frontState.is(MOVING_BLOCK) && frontState.getValue(FACING) == facing) {
            BlockEntity blockEntity = level.getBlockEntity(frontPos);

            if (blockEntity instanceof PistonMovingBlockEntity mbe) {
                return !mbe.isExtending() || !(mbe.getProgress(0.0F) < 0.5F || mbe.getLastTicked() == level.getGameTime() || level.isHandlingTick());
            }
        }

        return true;
    }

    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) || level.hasNeighborSignal(pos.above());
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int type, int data) {
        Direction facing = state.getValue(FACING);

        if (!world.isClientSide()) {
            boolean shouldExtend = this.hasNeighborSignal(world, pos, facing);

            if (shouldExtend && MotionType.isRetract(type)) {
                world.setBlock(pos, state.setValue(EXTENDED, true), UPDATE_CLIENTS);
                return false;
            }
            if (!shouldExtend && MotionType.isExtend(type)) {
                return false;
            }
        }
        if (MotionType.isExtend(type)) {
            if (!PistonUtils.move(world, pos, this, facing, true, ConfigurablePistonStructureResolver::resolve)) {
                return false;
            }

            world.setBlock(pos, state.setValue(EXTENDED, true), UPDATE_ALL | UPDATE_MOVE_BY_PISTON);
            playEvents(world, GameEvent.PISTON_EXTEND, pos);
        } else if (MotionType.isRetract(type)) {
            BlockPos headPos = pos.relative(facing);
            BlockEntity headBlockEntity = world.getBlockEntity(headPos);

            if (headBlockEntity instanceof BasicMovingBlockEntity mbe) {
                mbe.finalTick();
            }

            BlockState movingBaseState = MOVING_BLOCK.defaultBlockState()
                .setValue(MovingPistonBlock.FACING, facing)
                .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            BlockEntity movingBaseBlockEntity = MOVING_BLOCK.createMovingBlockEntity(
                pos,
                movingBaseState,
                this.defaultBlockState()
                    .setValue(FACING, Direction.from3DDataValue(data & 7)),
                facing,
                false,
                true
            );
            world.setBlock(pos, movingBaseState, UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
            world.setBlockEntity(movingBaseBlockEntity);

            world.updateNeighborsAt(pos, movingBaseState.getBlock());
            movingBaseState.updateNeighbourShapes(world, pos, UPDATE_CLIENTS);

            if (this.isSticky) {
                boolean droppedBlock = false;

                BlockPos frontPos = pos.relative(facing, 2);
                BlockState frontState = world.getBlockState(frontPos);

                if (frontState.is(MOVING_BLOCK)) {
                    BlockEntity frontBlockEntity = world.getBlockEntity(frontPos);

                    if (frontBlockEntity instanceof PistonMovingBlockEntity mbe && mbe.getDirection() == facing && mbe.isExtending()) {
                        mbe.finalTick();
                        droppedBlock = true;
                    }
                }
                if (!droppedBlock) {
                    if (type != MotionType.PULL || frontState.isAir() ||
                            (frontState.getPistonPushReaction() != PushReaction.NORMAL && !frontState.is(ModTags.PISTONS)) ||
                            !PistonUtils.isMovable(frontState, world, frontPos, facing.getOpposite(), false, facing)) {
                        world.removeBlock(headPos, false);
                    } else {
                        PistonUtils.move(world, pos, this, facing, false, ConfigurablePistonStructureResolver::resolve);
                    }
                }
            } else {
                world.removeBlock(headPos, false);
            }

            playEvents(world, GameEvent.PISTON_CONTRACT, pos);
        }

        return true;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(EXTENDED);
    }

    public boolean canPathfindThrough(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
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
        //level.gameEvent(event, pos);
    }
}
