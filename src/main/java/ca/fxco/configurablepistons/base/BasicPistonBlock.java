package ca.fxco.configurablepistons.base;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.Registerer;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonHandler;
import ca.fxco.configurablepistons.helpers.PistonUtils;
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
    public static final BooleanProperty EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE;
    protected static final VoxelShape EXTENDED_WEST_SHAPE;
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE;
    protected static final VoxelShape EXTENDED_NORTH_SHAPE;
    protected static final VoxelShape EXTENDED_UP_SHAPE;
    protected static final VoxelShape EXTENDED_DOWN_SHAPE;
    protected final boolean sticky;

    public final BasicPistonExtensionBlock EXTENSION_BLOCK;
    public final BasicPistonHeadBlock HEAD_BLOCK;

    public BasicPistonBlock(boolean sticky) {
        this(sticky, ConfigurablePistons.BASIC_MOVING_PISTON, ConfigurablePistons.BASIC_PISTON_HEAD);
    }

    public BasicPistonBlock(boolean sticky, BasicPistonExtensionBlock extensionBlock, BasicPistonHeadBlock headBlock) {
        super(FabricBlockSettings.copyOf(Blocks.PISTON));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, false));
        this.sticky = sticky;
        EXTENSION_BLOCK = extensionBlock;
        HEAD_BLOCK = headBlock;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(EXTENDED)) {
            switch (state.get(FACING)) {
                default:
                case UP: return EXTENDED_UP_SHAPE;
                case DOWN: return EXTENDED_DOWN_SHAPE;
                case NORTH: return EXTENDED_NORTH_SHAPE;
                case SOUTH: return EXTENDED_SOUTH_SHAPE;
                case EAST: return EXTENDED_EAST_SHAPE;
                case WEST: return EXTENDED_WEST_SHAPE;
            }
        } else {
            return VoxelShapes.fullCube();
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) this.tryMove(world, pos, state);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) this.tryMove(world, pos, state);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            if (!world.isClient && world.getBlockEntity(pos) == null) this.tryMove(world, pos, state);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(EXTENDED, false);
    }

    public void tryMove(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        boolean bl = this.shouldExtend(world, pos, direction);
        if (bl && !(Boolean)state.get(EXTENDED)) {
            if ((new PistonHandler(world, pos, direction, true)).calculatePush())
                world.addSyncedBlockEvent(pos, this, 0, direction.getId());
        } else if (!bl && state.get(EXTENDED)) {
            BlockPos blockPos = pos.offset(direction, 2);
            BlockState blockState = world.getBlockState(blockPos);
            int i = 1;
            if (blockState.isOf(EXTENSION_BLOCK) && blockState.get(FACING) == direction) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof PistonBlockEntity pistonBlockEntity) {
                    if (pistonBlockEntity.isExtending() &&
                            (pistonBlockEntity.getProgress(0.0F) < 0.5F ||
                            world.getTime() == pistonBlockEntity.getSavedWorldTime() ||
                            ((ServerWorld)world).isInBlockTick()))
                        i = 2;
                }
            }
            world.addSyncedBlockEvent(pos, this, i, direction.getId());
        }
    }

    public boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for(Direction direction : Direction.values()) {
            if (direction != pistonFace && world.isEmittingRedstonePower(pos.offset(direction), direction))
                return true;
        }
        if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
            return true;
        } else {
            BlockPos blockPos = pos.up();
            for(Direction direction : Direction.values()) {
                if (direction != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(direction), direction))
                    return true;
            }
            return false;
        }
    }

    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        Direction direction = state.get(FACING);
        if (!world.isClient) {
            boolean bl = this.shouldExtend(world, pos, direction);
            if (bl && (type == 1 || type == 2)) {
                world.setBlockState(pos, state.with(EXTENDED, true), Block.NOTIFY_LISTENERS);
                return false;
            }
            if (!bl && type == 0) return false;
        }
        if (type == 0) {
            if (!this.move(world, pos, direction, true)) return false;
            world.setBlockState(pos, state.with(EXTENDED, true), Block.NOTIFY_ALL | Block.MOVED);
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
            world.emitGameEvent(GameEvent.PISTON_EXTEND, pos);
        } else if (type == 1 || type == 2) {
            BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
            if (blockEntity instanceof PistonBlockEntity) ((PistonBlockEntity)blockEntity).finish();
            BlockState blockState = EXTENSION_BLOCK.getDefaultState()
                    .with(PistonExtensionBlock.FACING, direction)
                    .with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            world.setBlockState(pos, blockState, Block.NO_REDRAW | Block.FORCE_STATE);
            world.addBlockEntity(EXTENSION_BLOCK.createPistonBlockEntity(pos, blockState, this.getDefaultState().with(FACING, Direction.byId(data & 7)), direction, false, true));
            world.updateNeighbors(pos, blockState.getBlock());
            blockState.updateNeighbors(world, pos, Block.NOTIFY_LISTENERS);
            if (this.sticky) {
                BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
                BlockState state2 = world.getBlockState(blockPos);
                boolean bl2 = false;
                if (state2.isOf(EXTENSION_BLOCK)) {
                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
                    if (blockEntity2 instanceof PistonBlockEntity pistonBlockEntity) {
                        if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
                            pistonBlockEntity.finish();
                            bl2 = true;
                        }
                    }
                }
                if (!bl2) {
                    if (type != 1 || state2.isAir() || !PistonUtils.isMovable(state2, world, blockPos, direction.getOpposite(), false, direction) || state2.getPistonBehavior() != PistonBehavior.NORMAL && !state2.isIn(Registerer.PISTONS)) {
                        world.removeBlock(pos.offset(direction), false);
                    } else {
                        this.move(world, pos, direction, false);
                    }
                }
            } else {
                world.removeBlock(pos.offset(direction), false);
            }
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
            world.emitGameEvent(GameEvent.PISTON_CONTRACT, pos);
        }
        return true;
    }

    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurablePistonHandler(world, pos, dir, retract);
    }

    public boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
        BlockPos blockPos = pos.offset(dir);
        if (!retract && world.getBlockState(blockPos).isOf(HEAD_BLOCK))
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
        ConfigurablePistonHandler pistonHandler = getPistonHandler(world, pos, dir, retract);
        if (!pistonHandler.calculatePullPush(!retract)) return false;
        Map<BlockPos, BlockState> map = Maps.newHashMap();
        List<BlockPos> list = pistonHandler.getMovedBlocks();
        List<BlockState> list2 = Lists.newArrayList();
        for (BlockPos value : list) {
            BlockState blockState = world.getBlockState(value);
            list2.add(blockState);
            map.put(value, blockState);
        }
        List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
        BlockState[] blockStates = new BlockState[list.size() + list3.size()];
        Direction direction = retract ? dir : dir.getOpposite();
        int j = 0, k;
        BlockPos blockPos3;
        BlockState blockState2;
        for(k = list3.size() - 1; k >= 0; --k) {
            blockPos3 = list3.get(k);
            blockState2 = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            dropStacks(blockState2, world, blockPos3, blockEntity);
            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            if (!blockState2.isIn(BlockTags.FIRE)) world.addBlockBreakParticles(blockPos3, blockState2);
            blockStates[j++] = blockState2;
        }
        for(k = list.size() - 1; k >= 0; --k) {
            blockPos3 = list.get(k);
            blockState2 = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            BlockState blockState3 = EXTENSION_BLOCK.getDefaultState().with(FACING, dir);
            world.setBlockState(blockPos3, blockState3, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(EXTENSION_BLOCK.createPistonBlockEntity(blockPos3, blockState3, list2.get(k), dir, retract, false));
            blockStates[j++] = blockState2;
        }
        if (retract) {
            PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState4 = HEAD_BLOCK.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonType);
            blockState2 = EXTENSION_BLOCK.getDefaultState().with(PistonExtensionBlock.FACING, dir).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos);
            world.setBlockState(blockPos, blockState2, Block.NO_REDRAW | Block.MOVED);
            world.addBlockEntity(EXTENSION_BLOCK.createPistonBlockEntity(blockPos, blockState2, blockState4, dir, true, true));
        }
        BlockState blockState5 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos4 : map.keySet())
            world.setBlockState(blockPos4, blockState5, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
        BlockPos blockPos5;
        for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
            blockPos5 = entry.getKey();
            entry.getValue().prepare(world, blockPos5, 2);
            blockState5.updateNeighbors(world, blockPos5, Block.NOTIFY_LISTENERS);
            blockState5.prepare(world, blockPos5, 2);
        }
        j = 0;
        for(k = list3.size() - 1; k >= 0; --k) {
            blockState2 = blockStates[j++];
            blockPos5 = list3.get(k);
            blockState2.prepare(world, blockPos5, 2);
            world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
        }
        for(k = list.size() - 1; k >= 0; --k) world.updateNeighborsAlways(list.get(k), blockStates[j++].getBlock());
        if (retract) world.updateNeighborsAlways(blockPos, HEAD_BLOCK);
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

    static {
        EXTENDED = Properties.EXTENDED;
        EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    }
}
