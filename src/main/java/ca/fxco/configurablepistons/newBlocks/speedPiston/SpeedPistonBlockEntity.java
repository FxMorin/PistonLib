package ca.fxco.configurablepistons.newBlocks.speedPiston;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpeedPistonBlockEntity extends BasicPistonBlockEntity {

    public float speed;

    public SpeedPistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(1.0F, blockPos, blockState);
    }

    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state) {
        super(pos, state);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.SPEED_PISTON_BLOCK_ENTITY);
        this.speed = speed;
    }
    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this(speed, pos, state, pushedBlock, facing, extending, source, ConfigurablePistons.STRONG_MOVING_PISTON);
    }

    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source, BasicPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.SPEED_PISTON_BLOCK_ENTITY);
        this.speed = speed;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SpeedPistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0F) {
            if (world.isClient && blockEntity.field_26705 < 5) {
                ++blockEntity.field_26705;
            } else {
                world.removeBlockEntity(pos);
                blockEntity.markRemoved();
                if (world.getBlockState(pos).isOf(blockEntity.getExtensionBlock())) {
                    BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
                    if (blockState.isAir()) {
                        world.setBlockState(pos, blockEntity.pushedBlock, Block.NO_REDRAW | Block.FORCE_STATE | Block.MOVED);
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
            float f = blockEntity.progress + 0.5F * blockEntity.speed; // Add speed shift
            pushEntities(world, pos, f, blockEntity);
            moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
            blockEntity.progress = f;
            if (blockEntity.progress >= 1.0F) blockEntity.progress = 1.0F;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.speed = nbt.getFloat("speed");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat("speed", this.speed);
    }
}
