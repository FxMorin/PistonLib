package ca.fxco.configurablepistons.newBlocks.speedPiston;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.BasicPistonBlockEntity;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.helpers.Utils;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class SpeedPistonBlockEntity extends BasicPistonBlockEntity {

    /*
    TODO: Fix piston head visually being behind the piston
          Fix very slow pistons not having a hitbox, cause turns out pistons retracting in vanilla don't either they
          just do it so fast that you won't notice. Gosh I hate this game sometimes
          Remove speed & only use pistonOffset
     */

    private float speed;
    private double pistonOffset;

    public SpeedPistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(1.0F, blockPos, blockState);
    }

    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state) {
        super(pos, state);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.SPEED_PISTON_BLOCK_ENTITY);
        this.speed = speed;
        this.pistonOffset = 0.01 * speed;
    }
    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this(speed, pos, state, pushedBlock, facing, extending, source, ConfigurablePistons.STRONG_MOVING_PISTON);
    }

    public SpeedPistonBlockEntity(float speed, BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source, BasicPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source, extensionBlock);
        ((BlockEntityAccessor)this).setType(ConfigurablePistons.SPEED_PISTON_BLOCK_ENTITY);
        this.speed = speed;
        this.pistonOffset = 0.01 * speed;
    }

    public static void pushEntities(World world, BlockPos pos, float f, SpeedPistonBlockEntity blockEntity) {
        Direction dir = blockEntity.getMovementDirection();
        double d = f - blockEntity.progress;
        VoxelShape voxelShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
        if (voxelShape.isEmpty()) return;
        Box box = offsetHeadBox(pos, voxelShape.getBoundingBox(), blockEntity);
        // Here we use `Utils.stretchBlockBound()` in order to prevent the item frame dupe bug. We simply prevent the
        // bounding box from extending past the pistons back
        List<Entity> list = world.getOtherEntities(null, Utils.stretchBlockBound(box, dir, d).union(box));
        if (list.isEmpty()) return;
        List<Box> list2 = voxelShape.getBoundingBoxes();
        boolean bl = blockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
        Iterator<Entity> entityIterator = list.iterator();
        while(true) {
            Entity entity;
            while(true) {
                do {
                    if (!entityIterator.hasNext()) return;
                    entity = entityIterator.next();
                } while(entity.getPistonBehavior() == PistonBehavior.IGNORE);
                if (!bl) break;
                if (!(entity instanceof ServerPlayerEntity)) {
                    Vec3d vec3d = entity.getVelocity();
                    double e = vec3d.x;
                    double g = vec3d.y;
                    double h = vec3d.z;
                    switch (dir.getAxis()) {
                        case X -> e = dir.getOffsetX();
                        case Y -> g = dir.getOffsetY();
                        case Z -> h = dir.getOffsetZ();
                    }
                    entity.setVelocity(e, g, h);
                    break;
                }
            }
            double i = 0.0;
            for (Box box2 : list2) {
                Box box3 = Boxes.stretch(offsetHeadBox(pos, box2, blockEntity), dir, d);
                Box box4 = entity.getBoundingBox();
                if (box3.intersects(box4)) {
                    i = Math.max(i, getIntersectionSize(box3, dir, box4));
                    if (i >= d) break;
                }
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + blockEntity.pistonOffset;
            moveEntity(dir, entity, i, dir);
            //Push entities out of piston base. Always required!
            if (!blockEntity.extending && blockEntity.source) push(blockEntity, pos, entity, dir, d);
        }
    }

    public static void push(SpeedPistonBlockEntity be, BlockPos pos, Entity entity, Direction direction, double amount) {
        Box box = entity.getBoundingBox();
        Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos);
        if (box.intersects(box2)) {
            Direction direction2 = direction.getOpposite();
            double d = getIntersectionSize(box2, direction2, box) + be.pistonOffset;
            double e = getIntersectionSize(box2, direction2, box.intersection(box2)) + be.pistonOffset;
            if (Math.abs(d - e) < be.pistonOffset) {
                d = Math.min(d, amount) + be.pistonOffset;
                moveEntity(direction, entity, d, direction2);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, SpeedPistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0F) {
            if (world.isClient && blockEntity.field_26705 < Math.ceil(5/blockEntity.speed)) {
                ++blockEntity.field_26705; // deathTicks increment
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
            SpeedPistonBlockEntity.pushEntities(world, pos, f, blockEntity);
            moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
            blockEntity.progress = f;
            if (blockEntity.progress >= 1.0F) blockEntity.progress = 1.0F;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.speed = nbt.getFloat("speed");
        this.pistonOffset = 0.01 * this.speed;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat("speed", this.speed);
    }
}
