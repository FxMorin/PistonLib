package ca.fxco.pistonlib.blocks.pistons.mergePiston;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

import static net.minecraft.world.level.block.piston.PistonMovingBlockEntity.*;

public class MergeBlockEntity extends BlockEntity {

    protected final Map<Direction, MergeData> mergingBlocks = new HashMap<>();
    protected BlockState initialState;

    public MergeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MERGE_BLOCK_ENTITY, pos, state);
    }

    public MergeBlockEntity(BlockPos pos, BlockState state, BlockState initialState) {
        super(ModBlockEntities.MERGE_BLOCK_ENTITY, pos, state);

        this.initialState = initialState;
    }

    // Should always be called before calling `canMerge()`
    public boolean canMergeFromSide(Direction pushDirection) {
        return !mergingBlocks.containsKey(pushDirection);
    }

    public boolean canMerge(BlockState state, Direction dir) {
        ConfigurablePistonMerging merge = (ConfigurablePistonMerging) initialState.getBlock();
        return merge.canMultiMerge() && merge.canMultiMerge(state, initialState, dir, mergingBlocks);
    }

    public void doMerge(BlockState state, Direction dir) {
        mergingBlocks.put(dir, new MergeData(state));
    }

    public void doMerge(BlockState state, Direction dir, float speed) {
        MergeData data = new MergeData(state);
        data.setSpeed(speed);
        mergingBlocks.put(dir, data);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, MergeBlockEntity mergeBlockEntity) {
        byte count = 0;
        for (MergeData data : mergeBlockEntity.mergingBlocks.values()) {
            data.setLastProgress(data.getProgress());
            float lastProgress = data.getLastProgress();
            if (lastProgress >= 1.0F) {
                count++;
            }
            float f = lastProgress + 0.5F * data.getSpeed();
            mergeBlockEntity.moveCollidedEntities(f);
            //moveStuckEntities(level, blockPos, f, mergeBlockEntity);
            data.setProgress(Math.min(f, 1.0F));
        }
        if (count == mergeBlockEntity.mergingBlocks.size()) { // All ready
            level.removeBlockEntity(blockPos);
            mergeBlockEntity.setRemoved();

            BlockState initialState = mergeBlockEntity.initialState;
            if (initialState == null) return;
            ConfigurablePistonMerging merge = (ConfigurablePistonMerging) initialState.getBlock();
            BlockState newState = null;
            if (count > 1) {
                Map<Direction, BlockState> states = new HashMap<>();
                for (Map.Entry<Direction, MergeData> entry : mergeBlockEntity.mergingBlocks.entrySet()) {
                    states.put(entry.getKey(), entry.getValue().getState());
                }
                newState = merge.doMultiMerge(states, initialState);
            } else {
                for (Map.Entry<Direction, MergeData> entry : mergeBlockEntity.mergingBlocks.entrySet()) {
                    newState = merge.doMerge(entry.getValue().getState(), initialState, entry.getKey());
                    break;
                }
            }
            if (newState != null) {
                BlockState blockState2 = Block.updateFromNeighbourShapes(newState, level, blockPos);
                if (blockState2.isAir()) {
                    level.setBlock(blockPos, newState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
                    Block.updateOrDestroy(newState, blockState2, level, blockPos, Block.UPDATE_ALL);
                } else {
                    level.setBlock(blockPos, blockState2, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);
                    level.neighborChanged(blockPos, blockState2.getBlock(), blockPos);
                }
            }
        }
    }

    protected void moveCollidedEntities(float nextProgress) {
        VoxelShape initialShape = this.initialState.getCollisionShape(this.level, this.worldPosition);
        List<AABB>[] blockAabbs = new ArrayList[6];
        double[] deltaProgresses = new double[6];
        for (Map.Entry<Direction, MergeData> entry : this.mergingBlocks.entrySet()) {
            MergeData data = entry.getValue();
            Direction dir = entry.getKey();

            VoxelShape blockShape = data.getState().getCollisionShape(this.level, this.worldPosition);
            if (!blockShape.isEmpty()) {
                double maxProgress = (double)1.0F - data.progress;
                blockShape.move(
                        (double)this.worldPosition.getX() + maxProgress * (double)dir.getStepX(),
                        (double)this.worldPosition.getY() + maxProgress * (double)dir.getStepY(),
                        (double)this.worldPosition.getZ() + maxProgress * (double)dir.getStepZ()
                );
                double deltaProgress = nextProgress - data.progress;
                deltaProgresses[dir.ordinal()] = deltaProgress;
                blockAabbs[dir.ordinal()] = blockShape.toAabbs();
                initialShape = Shapes.join(initialShape, blockShape, BooleanOp.OR);
            }
        }

        AABB totalBlockBounds = initialShape.isEmpty() ? new AABB(0,0,0,1,1,1) : initialShape.bounds();

        List<Entity> entities = this.level.getEntities(null, totalBlockBounds);
        if (entities.isEmpty()) {
            return;
        }

        for (Entity entity : entities) {
            if (entity.getPistonPushReaction() == PushReaction.IGNORE) {
                continue;
            }

            AABB entityAabb = entity.getBoundingBox();

            for (Map.Entry<Direction, MergeData> entry : this.mergingBlocks.entrySet()) {
                Direction dir = entry.getKey();
                double movement = 0.0D;
                int ord = dir.ordinal();
                double delta = deltaProgresses[ord];
                for (AABB blockAabb : blockAabbs[ord]) {
                    blockAabb = PistonMath.getMovementArea(blockAabb, dir, delta);
                    if (blockAabb.intersects(entityAabb)) {
                        movement = Math.max(movement, getMovement(blockAabb, dir, entityAabb));

                        if (movement >= delta) {
                            break;
                        }
                    }
                }
                if (movement <= 0.0D) {
                    continue;
                }

                moveEntity(dir, entity, Math.min(movement, delta) + (0.01D * entry.getValue().getSpeed()), dir);
            }

            //fixEntityWithinPistonBase(entity, Direction.UP, 1, float movementMargin);
        }
    }

    /*protected void fixEntityWithinPistonBase(Entity entity, Direction moveDir, double deltaProgress, float movementMargin) {
        AABB entityAabb = entity.getBoundingBox();
        AABB baseAabb = Shapes.block().bounds().move(this.worldPosition);

        if (entityAabb.intersects(baseAabb)) {
            Direction opp = moveDir.getOpposite();
            double d = getMovement(baseAabb, opp, entityAabb) + this.movementMargin();
            double e = getMovement(baseAabb, opp, entityAabb.intersect(baseAabb)) + this.movementMargin();

            if (Math.abs(d - e) < this.movementMargin()) {
                moveEntity(moveDir, entity, Math.min(d, deltaProgress) + this.movementMargin(), opp);
            }
        }
    }*/

    protected static void moveEntity(Direction noclipDir, Entity entity, double amount, Direction moveDir) {
        NOCLIP.set(noclipDir);
        entity.move(MoverType.PISTON, new Vec3(
                amount * moveDir.getStepX(),
                amount * moveDir.getStepY(),
                amount * moveDir.getStepZ()
        ));
        NOCLIP.set(null);
    }

    private static double getMovement(AABB aABB, Direction direction, AABB aABB2) {
        switch (direction) {
            case EAST:
                return aABB.maxX - aABB2.minX;
            case WEST:
                return aABB2.maxX - aABB.minX;
            case UP:
            default:
                return aABB.maxY - aABB2.minY;
            case DOWN:
                return aABB2.maxY - aABB.minY;
            case SOUTH:
                return aABB.maxZ - aABB2.minZ;
            case NORTH:
                return aABB2.maxZ - aABB.minZ;
        }
    }

    public float getProgress(float f, float progress, float lastProgress) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.lerp(f, lastProgress, progress);
    }

    public float getXOff(Direction dir, float f, float progress, float lastProgress) {
        return (float)dir.getStepX() * (this.getProgress(f, progress, lastProgress) - 1);
    }

    public float getYOff(Direction dir, float f, float progress, float lastProgress) {
        return (float)dir.getStepY() * (this.getProgress(f, progress, lastProgress) - 1);
    }

    public float getZOff(Direction dir, float f, float progress, float lastProgress) {
        return (float)dir.getStepZ() * (this.getProgress(f, progress, lastProgress) - 1);
    }

    public BlockState getInitialState() {
        return this.initialState;
    }

    public Map<Direction, MergeData> getMergingBlocks() {
        return this.mergingBlocks;
    }

    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        HolderGetter<Block> holderGetter = this.level != null ?
                this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.initialState = NbtUtils.readBlockState(holderGetter, compoundTag.getCompound("initialState"));
        for (Direction dir : Direction.values()) {
            if (compoundTag.contains("dir" + dir.ordinal(), Tag.TAG_COMPOUND)) {
                CompoundTag tag = compoundTag.getCompound("dir" + dir.ordinal());
                mergingBlocks.put(dir, MergeData.loadNbt(holderGetter, tag));
            }
        }
    }

    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("initialState", NbtUtils.writeBlockState(initialState));
        for (Map.Entry<Direction, MergeData> entry : mergingBlocks.entrySet()) {
            compoundTag.put("dir" + entry.getKey().ordinal(), MergeData.writeNbt(entry.getValue()));
        }
    }

    public static class MergeData {

        private final BlockState state;
        private float progress;
        private float lastProgress;
        private float speed = 1F;

        public MergeData(BlockState state) {
            this.state = state;
        }

        public boolean isFinished() {
            return progress >= 1.0F;
        }

        public BlockState getState() {
            return state;
        }

        public float getProgress() {
            return progress;
        }

        public float getLastProgress() {
            return lastProgress;
        }

        public float getSpeed() {
            return speed;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public void setLastProgress(float lastProgress) {
            this.lastProgress = lastProgress;
        }

        public void setAllProgress(float progress) {
            this.progress = this.lastProgress = progress;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public static CompoundTag writeNbt(MergeData data) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("state", NbtUtils.writeBlockState(data.getState()));
            if (data.getProgress() == data.getLastProgress()) {
                compoundTag.putFloat("progress", data.getProgress());
            } else {
                compoundTag.putFloat("progress", data.getProgress());
                compoundTag.putFloat("lastProgress", data.getLastProgress());
            }
            if (data.getSpeed() != 1F) {
                compoundTag.putFloat("speed", data.getSpeed());
            }
            return compoundTag;
        }

        public static MergeData loadNbt(HolderGetter<Block> holderGetter, CompoundTag compoundTag) {
            BlockState state = NbtUtils.readBlockState(holderGetter, compoundTag.getCompound("state"));
            MergeData data = new MergeData(state);
            if (compoundTag.contains("lastProgress", Tag.TAG_FLOAT)) {
                data.setProgress(compoundTag.getFloat("progress"));
                data.setLastProgress(compoundTag.getFloat("lastProgress"));
            } else {
                data.setAllProgress(compoundTag.getFloat("float"));
            }
            if (compoundTag.contains("speed")) {
                data.setSpeed(compoundTag.getFloat("speed"));
            }
            return data;
        }
    }
}
