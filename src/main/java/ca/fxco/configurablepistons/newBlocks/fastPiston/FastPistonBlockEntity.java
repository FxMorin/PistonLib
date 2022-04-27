package ca.fxco.configurablepistons.newBlocks.fastPiston;

import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FastPistonBlockEntity extends BasicPistonBlockEntity {
    public FastPistonBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasicPistonBlockEntity blockEntity) {
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
            float f = blockEntity.progress + 0.5F;
            pushEntities(world, pos, f, blockEntity);
            moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
            blockEntity.progress = f;
            if (blockEntity.progress >= 1.0F) blockEntity.progress = 1.0F;
        }
    }
}
