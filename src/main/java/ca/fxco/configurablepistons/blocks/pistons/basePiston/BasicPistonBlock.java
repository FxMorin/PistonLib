package ca.fxco.configurablepistons.blocks.pistons.basePiston;

import ca.fxco.configurablepistons.base.ModTags;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonHandler;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;

public class BasicPistonBlock extends FacingBlock {
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    public final boolean sticky;

    public BasicPistonExtensionBlock EXTENSION_BLOCK;
    public BasicPistonHeadBlock HEAD_BLOCK;

    public BasicPistonBlock(boolean sticky) {
        this(sticky, ModBlocks.BASIC_MOVING_PISTON, ModBlocks.BASIC_PISTON_HEAD);
    }

    public BasicPistonBlock(boolean sticky, AbstractBlock.Settings settings) {
        this(sticky, settings, ModBlocks.BASIC_MOVING_PISTON, ModBlocks.BASIC_PISTON_HEAD);
    }

    public BasicPistonBlock(boolean sticky, BasicPistonExtensionBlock extensionBlock, BasicPistonHeadBlock headBlock) {
        this(sticky, FabricBlockSettings.copyOf(Blocks.PISTON), extensionBlock, headBlock);
    }

    public BasicPistonBlock(boolean sticky, AbstractBlock.Settings settings,
                            BasicPistonExtensionBlock extensionBlock, BasicPistonHeadBlock headBlock) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, false));
        this.sticky = sticky;
        EXTENSION_BLOCK = extensionBlock;
        HEAD_BLOCK = headBlock;
    }

    public BasicPistonExtensionBlock getExtensionBlock() {
        return EXTENSION_BLOCK;
    }

    public BasicPistonHeadBlock getHeadBlock() {
        return HEAD_BLOCK;
    }

    public void setExtensionBlock(BasicPistonExtensionBlock extensionBlock) {
        EXTENSION_BLOCK = extensionBlock;
    }

    public void setHeadBlock(BasicPistonHeadBlock headBlock) {
        HEAD_BLOCK = headBlock;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(EXTENDED)) {
            return switch (state.get(FACING)) {
                default -> EXTENDED_UP_SHAPE;
                case DOWN -> EXTENDED_DOWN_SHAPE;
                case NORTH -> EXTENDED_NORTH_SHAPE;
                case SOUTH -> EXTENDED_SOUTH_SHAPE;
                case EAST -> EXTENDED_EAST_SHAPE;
                case WEST -> EXTENDED_WEST_SHAPE;
            };
        }
        return VoxelShapes.fullCube();
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) this.tryMove(world, pos, state);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos from, boolean noti) {
        if (!world.isClient) this.tryMove(world, pos, state);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient && world.getBlockEntity(pos) == null)
            this.tryMove(world, pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(EXTENDED, false);
    }

    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurablePistonHandler(world, pos, dir, retract);
    }

    public void tryMove(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        boolean bl = this.shouldExtend(world, pos, direction);
        if (bl && !(Boolean)state.get(EXTENDED)) {
            if ((getPistonHandler(world, pos, direction, true)).calculatePullPush(false))
                world.addSyncedBlockEvent(pos, this, 0, direction.getId());
        } else if (!bl && state.get(EXTENDED)) {
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.isOf(EXTENSION_BLOCK) && blockState.get(FACING) == direction) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof PistonBlockEntity pistonBlockEntity &&
                        pistonBlockEntity.isExtending() && (
                                pistonBlockEntity.getProgress(0.0F) < 0.5F ||
                                world.getTime() == pistonBlockEntity.getSavedWorldTime() ||
                                ((ServerWorld)world).isInBlockTick())) {
                    i = 2;
                }
            }
            world.addSyncedBlockEvent(pos, this, i, direction.getId());
        }
    }

    public boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for(Direction dir : Direction.values())
            if (dir != pistonFace && world.isEmittingRedstonePower(pos.offset(dir), dir))
                return true;
        if (world.isEmittingRedstonePower(pos, Direction.DOWN))
            return true;
        BlockPos blockPos = pos.up();
        for(Direction dir : Direction.values())
            if (dir != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(dir), dir))
                return true;
        return false;
    }

    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        Direction dir = state.get(FACING);
        if (!world.isClient) {
            boolean bl = this.shouldExtend(world, pos, dir);
            if (bl && (type == 1 || type == 2)) {
                world.setBlockState(pos, state.with(EXTENDED, true), Block.NOTIFY_LISTENERS);
                return false;
            }
            if (!bl && type == 0) return false;
        }
        if (type == 0) {
            if (!PistonUtils.move(world, pos, this, dir, true, ConfigurablePistonHandler::calculatePullPush))
                return false;
            world.setBlockState(pos, state.with(EXTENDED, true), Block.NOTIFY_ALL | Block.MOVED);
            playEvents(world, GameEvent.PISTON_EXTEND, pos);
        } else if (type == 1 || type == 2) {
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(dir));
            if (blockEntity instanceof BasicPistonBlockEntity bpbe) bpbe.finish();
            BlockState blockState = EXTENSION_BLOCK.getDefaultState()
                    .with(PistonExtensionBlock.FACING, dir)
                    .with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            world.setBlockState(pos, blockState, Block.NO_REDRAW | Block.FORCE_STATE);
            world.addBlockEntity(EXTENSION_BLOCK.createPistonBlockEntity(
                    pos,
                    blockState,
                    this.getDefaultState().with(FACING, Direction.byId(data & 7)),
                    dir,
                    false,
                    true
            ));
            world.updateNeighbors(pos, blockState.getBlock());
            blockState.updateNeighbors(world, pos, Block.NOTIFY_LISTENERS);
            if (this.sticky) {
                BlockPos blockPos = pos.add(dir.getOffsetX() * 2, dir.getOffsetY() * 2, dir.getOffsetZ() * 2);
                BlockState state2 = world.getBlockState(blockPos);
                boolean bl2 = false;
                if (state2.isOf(EXTENSION_BLOCK)) {
                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
                    if (blockEntity2 instanceof PistonBlockEntity pistonBlockEntity &&
                            pistonBlockEntity.getFacing() == dir && pistonBlockEntity.isExtending()) {
                        pistonBlockEntity.finish();
                        bl2 = true;
                    }
                }
                if (!bl2) {
                    if (type != 1 || state2.isAir() ||
                            (state2.getPistonBehavior() != PistonBehavior.NORMAL && !state2.isIn(ModTags.PISTONS)) ||
                            !PistonUtils.isMovable(state2, world, blockPos, dir.getOpposite(), false, dir)) {
                        world.removeBlock(pos.offset(dir), false);
                    } else {
                        PistonUtils.move(world, pos, this, dir, false, ConfigurablePistonHandler::calculatePullPush);
                    }
                }
            } else {
                world.removeBlock(pos.offset(dir), false);
            }
            playEvents(world, GameEvent.PISTON_CONTRACT, pos);
        }
        return true;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED);
    }

    public boolean hasSidedTransparency(BlockState state) {
        return state.get(EXTENDED);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public void playEvents(World world, GameEvent event, BlockPos pos) {
        world.playSound(
                null,
                pos,
                event == GameEvent.PISTON_CONTRACT ?
                        SoundEvents.BLOCK_PISTON_CONTRACT : SoundEvents.BLOCK_PISTON_EXTEND,
                SoundCategory.BLOCKS,
                0.5F,
                world.random.nextFloat() * 0.25F + 0.6F
        );
        world.emitGameEvent(event, pos);
    }
}
