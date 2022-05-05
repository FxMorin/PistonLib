package ca.fxco.configurablepistons.blocks.pistons.basePiston;

import ca.fxco.configurablepistons.ConfigurablePistons;
import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.mixin.accessors.BlockEntityAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class BasicPistonBlockEntity extends PistonBlockEntity {

    /*
     * This class overrides all the non-static methods of PistonBlockEntity
     */

    public final BasicPistonExtensionBlock EXTENSION_BLOCK;

    public BasicPistonBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ModBlocks.BASIC_MOVING_PISTON);
    }

    public BasicPistonBlockEntity(BlockPos pos, BlockState state, BasicPistonExtensionBlock extensionBlock) {
        super(pos, state);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.BASIC_PISTON_BLOCK_ENTITY);
        EXTENSION_BLOCK = extensionBlock;
    }

    public BasicPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source) {
        this(pos, state, pushedBlock, facing, extending, source, ModBlocks.BASIC_MOVING_PISTON);
    }
    public BasicPistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing,
                                  boolean extending, boolean source, BasicPistonExtensionBlock extensionBlock) {
        super(pos, state, pushedBlock, facing, extending, source);
        ((BlockEntityAccessor)this).setType(ModBlockEntities.BASIC_PISTON_BLOCK_ENTITY);
        EXTENSION_BLOCK = extensionBlock;
    }

    public BasicPistonExtensionBlock getExtensionBlock() {
        return EXTENSION_BLOCK;
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public float getAmountExtended(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    @Override
    public BlockState getHeadBlockState() {
        // Removed setting the type since this is currently only used for collision shape
        return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof BasicPistonBlock ?
                ModBlocks.BASIC_PISTON_HEAD.getDefaultState()
                        .with(BasicPistonHeadBlock.SHORT, this.progress > 0.25F)
                        .with(BasicPistonHeadBlock.FACING, this.pushedBlock.get(BasicPistonBlock.FACING)) :
                this.pushedBlock;
    }

    public void pushEntities(World world, BlockPos pos, float f) {
        Direction dir = this.getMovementDirection();
        double d = f - this.progress;
        VoxelShape voxelShape = this.getHeadBlockState().getCollisionShape(world, pos);
        if (voxelShape.isEmpty()) return;
        Box box = offsetHeadBox(pos, voxelShape.getBoundingBox(), this);
        List<Entity> list = world.getOtherEntities(null, Boxes.stretch(box, dir, d).union(box));
        if (list.isEmpty()) return;
        List<Box> voxelBounds = voxelShape.getBoundingBoxes();
        boolean bl = this.pushedBlock.isOf(Blocks.SLIME_BLOCK);
        for (Entity entity : list) {
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            if (bl) {
                if (entity instanceof ServerPlayerEntity) continue;
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
            }
            double i = 0.0;
            for (Box boxes : voxelBounds) {
                Box box3 = Boxes.stretch(offsetHeadBox(pos, boxes, this), dir, d);
                Box box4 = entity.getBoundingBox();
                if (box3.intersects(box4) && (i = Math.max(i, getIntersectionSize(box3, dir, box4))) >= d) break;
            }
            if (i <= 0.0) continue;
            moveEntity(dir, entity, Math.min(i, d) + 0.01, dir);
            if (!this.extending && this.source) push(pos, entity, dir, d);
        }
    }

    public static void moveEntity(Direction direction, Entity entity, double d, Direction dir) {
        field_12205.set(direction);
        entity.move(MovementType.PISTON, new Vec3d(
                d * (double)dir.getOffsetX(),
                d * (double)dir.getOffsetY(),
                d * (double)dir.getOffsetZ()
        ));
        field_12205.set(null);
    }

    public static void moveEntitiesInHoneyBlock(World world, BlockPos pos, float f, BasicPistonBlockEntity be) {
        if (be.isPushingHoneyBlock()) {
            Direction direction = be.getMovementDirection();
            if (direction.getAxis().isHorizontal()) {
                double d = be.pushedBlock.getCollisionShape(world, pos).getMax(Direction.Axis.Y);
                Box box = offsetHeadBox(pos, new Box(0.0, d, 0.0, 1.0, 1.5, 1.0), be);
                double e = f - be.progress;
                List<Entity> list = world.getOtherEntities(null, box, entity -> canMoveEntity(box, entity));
                for (Entity entity : list) {
                    moveEntity(direction, entity, e, direction);
                }
            }
        }
    }

    public static boolean canMoveEntity(Box box, Entity entity) {
        return entity.getPistonBehavior() == PistonBehavior.NORMAL && entity.isOnGround() &&
                entity.getX() >= box.minX &&
                entity.getX() <= box.maxX &&
                entity.getZ() >= box.minZ &&
                entity.getZ() <= box.maxZ;
    }

    @Override
    public boolean isPushingHoneyBlock() {
        return this.pushedBlock.isOf(Blocks.HONEY_BLOCK);
    }

    @Override
    public Direction getMovementDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    public static double getIntersectionSize(Box box, Direction direction, Box box2) {
        return switch (direction) {
            default -> box.maxY - box2.minY;
            case EAST -> box.maxX - box2.minX;
            case WEST -> box2.maxX - box.minX;
            case DOWN -> box2.maxY - box.minY;
            case SOUTH -> box.maxZ - box2.minZ;
            case NORTH -> box2.maxZ - box.minZ;
        };
    }

    public static Box offsetHeadBox(BlockPos pos, Box box, BasicPistonBlockEntity blockEntity) {
        double d = blockEntity.getAmountExtended(blockEntity.progress);
        return box.offset(
                (double)pos.getX() + d * (double)blockEntity.facing.getOffsetX(),
                (double)pos.getY() + d * (double)blockEntity.facing.getOffsetY(),
                (double)pos.getZ() + d * (double)blockEntity.facing.getOffsetZ()
        );
    }

    public static void push(BlockPos pos, Entity entity, Direction direction, double amount) {
        Box box = entity.getBoundingBox();
        Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos);
        if (box.intersects(box2)) {
            Direction dir = direction.getOpposite();
            double d = getIntersectionSize(box2, dir, box) + 0.01;
            double e = getIntersectionSize(box2, dir, box.intersection(box2)) + 0.01;
            if (Math.abs(d - e) < 0.01) moveEntity(direction, entity, Math.min(d, amount) + 0.01, dir);
        }
    }

    @Override
    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }

    @Override
    public void finish() {
        if (this.world != null && (this.lastProgress < 1.0F || this.world.isClient)) {
            this.progress = 1.0F;
            this.lastProgress = this.progress;
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(EXTENSION_BLOCK)) {
                BlockState blockState = this.source ?
                        Blocks.AIR.getDefaultState() : Block.postProcessState(this.pushedBlock, this.world, this.pos);
                this.world.setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasicPistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0F) {
            if (world.isClient && blockEntity.field_26705 < 5) {
                ++blockEntity.field_26705;
                return;
            }
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
        } else {
            float f = blockEntity.progress + 0.5F;
            blockEntity.pushEntities(world, pos, f);
            moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
            blockEntity.progress = f;
            if (blockEntity.progress >= 1.0F) blockEntity.progress = 1.0F;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.pushedBlock = NbtHelper.toBlockState(nbt.getCompound("blockState"));
        this.facing = Direction.byId(nbt.getInt("facing"));
        this.progress = nbt.getFloat("progress");
        if (ConfigurablePistons.PISTON_PROGRESS_FIX) {
            this.lastProgress = nbt.contains("lastProgress") ? nbt.getFloat("lastProgress") : this.progress;
        } else {
            this.lastProgress = this.progress;
        }
        this.extending = nbt.getBoolean("extending");
        this.source = nbt.getBoolean("source");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        nbt.putInt("facing", this.facing.getId());
        if (ConfigurablePistons.PISTON_PROGRESS_FIX) {
            nbt.putFloat("progress", this.progress);
            nbt.putFloat("lastProgress", this.lastProgress);
        } else {
            nbt.putFloat("progress", this.lastProgress);
        }
        nbt.putBoolean("extending", this.extending);
        nbt.putBoolean("source", this.source);
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
            blockState = ModBlocks.BASIC_PISTON_HEAD.getDefaultState()
                    .with(PistonHeadBlock.FACING, this.facing)
                    .with(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
        } else {
            blockState = this.pushedBlock;
        }
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(voxelShape, blockState.getCollisionShape(world, pos).offset(d, e, g));
    }

    @Override
    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }
}
