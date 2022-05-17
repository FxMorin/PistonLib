package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import ca.fxco.configurablepistons.pistonLogic.PistonUtils;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurableLongPistonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LongPistonBlockEntity extends BasicPistonBlockEntity {

    public static final int MAX_ARM_LENGTH = 11;

    public final int maxLength;
    public int length;
    public boolean arm;
    public boolean skipCheck = false;

    public LongPistonBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.maxLength = MAX_ARM_LENGTH;
        this.length = MAX_ARM_LENGTH;
        this.arm = false;
        ((BlockEntityAccessor)this).setType(ModBlockEntities.LONG_PISTON_BLOCK_ENTITY);
    }

    public LongPistonBlockEntity(BlockPos pos, BlockState state, int maxLength, int length, boolean arm) {
        this(pos, state, maxLength, length, arm, ModBlocks.LONG_MOVING_PISTON);
    }

    public LongPistonBlockEntity(BlockPos pos, BlockState state, int maxLength, int length, boolean arm,
                                 LongPistonExtensionBlock extensionBlock) {
        super(pos, state, extensionBlock);
        this.maxLength = maxLength;
        this.length = length;
        this.arm = arm;
        ((BlockEntityAccessor)this).setType(ModBlockEntities.LONG_PISTON_BLOCK_ENTITY);
    }

    public LongPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                 boolean extending, boolean source, int maxLength, int length, boolean arm) {
        this(pos, state, pushedBlock, facing, extending, source, maxLength, length, arm, ModBlocks.LONG_MOVING_PISTON);
    }
    public LongPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock,
                                 Direction facing, boolean extending, boolean source, int maxLength,
                                 int length, boolean arm, LongPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        this.maxLength = maxLength;
        this.length = length;
        this.arm = arm;
        ((BlockEntityAccessor)this).setType(ModBlockEntities.LONG_PISTON_BLOCK_ENTITY);
    }

    public boolean isArm() {
        return this.arm;
    }

    public void skipCheck() {
        this.skipCheck = true;
    }

    @Override
    public BlockState getCollisionState() {
        if (!this.isExtending()) {
            return this.isSource() && this.pushedBlock.getBlock() instanceof BasicPistonBlock ?
                    ModBlocks.BASIC_PISTON_HEAD.getDefaultState()
                            .with(Properties.SHORT, this.progress > 0.25F)
                            .with(Properties.FACING, this.pushedBlock.get(Properties.FACING)) :
                    this.pushedBlock;
        } else {
            return this.isArm() && this.pushedBlock.getBlock() instanceof BasicPistonBlock ?
                    ModBlocks.LONG_PISTON_ARM.getDefaultState()
                            .with(Properties.SHORT, this.isSource() && this.progress > 0.25F)
                            .with(Properties.FACING, this.pushedBlock.get(Properties.FACING)) :
                    this.pushedBlock;
        }
    }

    public ConfigurableLongPistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurableLongPistonHandler(world, pos, dir, retract, EXTENSION_BLOCK);
    }

    protected static boolean onFinishMovement(LongPistonBlockEntity be) {
        BlockPos arriveAt = be.pos.offset(be.facing);
        BlockEntity blockEntity = be.world.getBlockEntity(arriveAt);
        //Should tick before the other piston block entities because it was created first. But not really V
        // Compensate for mojang's terrible code - Carpet-Fixes Rule: `reloadUpdateOrderFix` is the proper fix
        if (blockEntity instanceof LongPistonBlockEntity bpbe && ((bpbe.length == 0) != bpbe.isArm()) &&
                bpbe.length == be.length-1 && bpbe.facing == be.facing) bpbe.finish();
        /*if (be.skipCheck || (be.length < be.maxLength-1 &&
                be.getPistonHandler(be.world, arriveAt, be.facing, be.extending)
                        .calculatePullPushWithBE(!be.extending, LongPistonBlockEntity::skipCheck))) {
            BlockState blockState = be.world.getBlockState(be.pos);
            be.world.setBlockState(arriveAt, blockState, Block.FORCE_STATE);
            be.world.addBlockEntity(((LongPistonExtensionBlock) be.EXTENSION_BLOCK).createLongPistonBlockEntity(
                    arriveAt,
                    blockState,
                    be.pushedBlock,
                    be.facing,
                    be.extending,
                    false,
                    be.maxLength - 1,
                    be.length,
                    be.arm
            ));
            if (be.isSource()) {
                BlockPos pos = be.pos.offset(be.facing);
                // TODO: Make more configurable
                BlockState armState = ModBlocks.LONG_PISTON_ARM.getDefaultState()
                        .with(Properties.SHORT, false)
                        .with(Properties.FACING, be.facing)
                        .with(Properties.PISTON_TYPE, blockState.get(Properties.PISTON_TYPE));
                be.world.setBlockState(pos, armState, Block.FORCE_STATE);
                be.world.addBlockEntity(((LongPistonExtensionBlock) be.EXTENSION_BLOCK).createLongPistonBlockEntity(
                        pos,
                        Blocks.EMERALD_BLOCK.getDefaultState(),
                        Blocks.EMERALD_BLOCK.getDefaultState(),
                        be.facing,
                        be.extending,
                        true,
                        be.maxLength,
                        be.length + 1,
                        true
                ));
            } else {
                be.world.removeBlockEntity(be.pos);
                be.markRemoved();
            }
            return false;
        }*/
        // TODO: Make more configurable          V
        PistonUtils.move(be.world, be.pos, ModBlocks.LONG_PISTON, be.facing, be.extending, (pistonHandler, pull) ->
                ((ConfigurableLongPistonHandler)pistonHandler).calculateLongPullPush(pull,LongPistonBlockEntity::skipCheck));
        return true;
    }

    @Override
    public void finish() {
        if (this.world != null && (this.lastProgress < 1.0F || this.world.isClient)) {
            if (onFinishMovement(this)) super.finish();
        } else {
            super.finish();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, LongPistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0F) {
            if (world.isClient && blockEntity.field_26705 < 5) {
                ++blockEntity.field_26705;
                return;
            }
            if (onFinishMovement(blockEntity)) {
                world.removeBlockEntity(pos);
                blockEntity.markRemoved();
                if (world.getBlockState(pos).isOf(blockEntity.getExtensionBlock())) {
                    BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
                    if (blockState.isAir()) {
                        world.setBlockState(pos, blockEntity.pushedBlock,
                                Block.NO_REDRAW | Block.FORCE_STATE | Block.MOVED);
                        Block.replace(blockEntity.pushedBlock, blockState, world, pos, 3);
                    } else {
                        if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) {
                            blockState = blockState.with(Properties.WATERLOGGED, false);
                        }
                        world.setBlockState(pos, blockState, Block.NOTIFY_ALL | Block.MOVED);
                        world.updateNeighbor(pos, blockState.getBlock(), pos);
                    }
                }
            }
        } else {
            float f = blockEntity.progress + 0.5F * 0.01F;
            blockEntity.pushEntities(world, pos, f);
            moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
            blockEntity.progress = f;
            if (blockEntity.progress >= 1.0F) blockEntity.progress = 1.0F;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        VoxelShape voxelShape;
        if (!this.extending && this.source && this.pushedBlock.getBlock() instanceof BasicPistonBlock) {
            voxelShape = this.pushedBlock.with(BasicPistonBlock.EXTENDED, true).getCollisionShape(world, pos);
        } else {
            voxelShape = VoxelShapes.empty();
        }
        Direction direction = field_12205.get();
        if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
            return voxelShape;
        }
        BlockState blockState;
        if (this.isSource()) {
            if (this.isArm()) {
                blockState = ModBlocks.LONG_PISTON_ARM.getDefaultState()
                        .with(PistonHeadBlock.FACING, this.facing)
                        .with(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
            } else {
                blockState = ModBlocks.BASIC_PISTON_HEAD.getDefaultState()
                        .with(PistonHeadBlock.FACING, this.facing)
                        .with(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
            }
        } else {
            blockState = this.pushedBlock;
        }
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(voxelShape, blockState.getCollisionShape(world, pos).offset(d, e, g));
    }
}
