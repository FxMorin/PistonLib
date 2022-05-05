package ca.fxco.configurablepistons.blocks.pistons.fastPiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FastPistonBlockEntity extends BasicPistonBlockEntity {

    public FastPistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.FAST_PISTON_BLOCK_ENTITY);
    }
    public FastPistonBlockEntity(BlockPos pos, BlockState state, FastPistonExtensionBlock extensionBlock) {
        super(pos, state, extensionBlock);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.FAST_PISTON_BLOCK_ENTITY);
    }
    public FastPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source, FastPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.FAST_PISTON_BLOCK_ENTITY);
    }

    @Override
    public void finish() {
        if (this.world != null) {
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(EXTENSION_BLOCK)) {
                BlockState blockState;
                if (this.source) {
                    blockState = Blocks.AIR.getDefaultState();
                } else {
                    blockState = Block.postProcessState(this.pushedBlock, this.world, this.pos);
                }
                this.world.setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasicPistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.pushEntities(world, pos, 1.0F);
        moveEntitiesInHoneyBlock(world, pos, 1.0F, blockEntity);
        blockEntity.progress = 1.0F;
        world.removeBlockEntity(pos);
        blockEntity.markRemoved();
        if (!world.getBlockState(pos).isOf(blockEntity.getExtensionBlock())) return;
        BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
        if (blockState.isAir()) {
            world.setBlockState(pos, blockEntity.pushedBlock, Block.NO_REDRAW | Block.FORCE_STATE | Block.MOVED);
            Block.replace(blockEntity.pushedBlock, blockState, world, pos, 3);
        } else {
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL | Block.MOVED);
            world.updateNeighbor(pos, blockState.getBlock(), pos);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.pushedBlock = NbtHelper.toBlockState(nbt.getCompound("blockState"));
        this.facing = Direction.byId(nbt.getInt("facing"));
        this.progress = nbt.getFloat("progress");
        this.extending = nbt.getBoolean("extending");
        this.source = nbt.getBoolean("source");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        nbt.putInt("facing", this.facing.getId());
        nbt.putBoolean("extending", this.extending);
        nbt.putBoolean("source", this.source);
    }
}
