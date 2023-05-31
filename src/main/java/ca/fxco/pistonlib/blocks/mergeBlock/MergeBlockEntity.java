package ca.fxco.pistonlib.blocks.mergeBlock;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.api.pistonlib.block.MovingTickable;
import lombok.Getter;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.minecraft.world.level.block.piston.PistonMovingBlockEntity.*;

@Getter
public class MergeBlockEntity extends BlockEntity {

    protected final Map<Direction, MergeData> mergingBlocks = new HashMap<>();
    protected BlockState initialState;
    protected @Nullable BlockEntity initialBlockEntity;

    public MergeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MERGE_BLOCK_ENTITY, pos, state);
    }

    public MergeBlockEntity(BlockPos pos, BlockState state, BlockState initialState) {
        this(pos, state, initialState, null);
    }

    public MergeBlockEntity(BlockPos pos, BlockState state, BlockState initialState, BlockEntity initialBlockEntity) {
        super(ModBlockEntities.MERGE_BLOCK_ENTITY, pos, state);

        this.initialState = initialState;
        this.initialBlockEntity = initialBlockEntity;
    }

    // Should always be called before calling `canMerge()`
    public boolean canMergeFromSide(Direction pushDirection) {
        return !mergingBlocks.containsKey(pushDirection);
    }

    public boolean canMerge(BlockState state, Direction dir) {
        Block merge = initialState.getBlock();
        if (merge.pl$canMultiMerge() &&
                merge.pl$canMultiMerge(state, level, worldPosition, initialState, dir, mergingBlocks)) {
            return initialBlockEntity == null || (!merge.pl$getBlockEntityMergeRules().checkMerge() ||
                    initialBlockEntity.pl$canMultiMerge(state, initialState, dir, mergingBlocks));
        }
        return false;
    }

    public void doMerge(BlockState state, Direction dir) {
        mergingBlocks.put(dir, new MergeData(state));
    }

    public void doMerge(BlockState state, Direction dir, float speed) {
        MergeData data = new MergeData(state);
        data.setSpeed(speed);
        mergingBlocks.put(dir, data);
    }

    public void doMerge(BlockState state, BlockEntity blockEntity, Direction dir, float speed) {
        MergeData data = new MergeData(blockEntity, state);
        data.setSpeed(speed);
        mergingBlocks.put(dir, data);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, MergeBlockEntity mergeBlockEntity) {
        byte count = 0;
        for (Map.Entry<Direction, MergeData> entry : mergeBlockEntity.mergingBlocks.entrySet()) {
            MergeData data = entry.getValue();
            data.setLastProgress(data.getProgress());
            float lastProgress = data.getLastProgress();
            if (lastProgress >= 1.0F) {
                count++;
            }
            float speed = data.getSpeed();
            float f = lastProgress + 0.5F * speed;
            mergeBlockEntity.moveCollidedEntities(f);
            //moveStuckEntities(level, blockPos, f, mergeBlockEntity);
            data.setProgress(Math.min(f, 1.0F));
            if (PistonLibConfig.tickingApi && lastProgress < 1.0F) {
                data.onMovingTick(level, blockPos, entry.getKey());
            }
        }
        if (count == mergeBlockEntity.mergingBlocks.size()) { // All ready
            level.removeBlockEntity(blockPos);
            mergeBlockEntity.setRemoved();

            BlockState initialState = mergeBlockEntity.initialState;
            if (initialState == null) return;
            Block merge = initialState.getBlock();
            BlockState newState = null;
            if (count > 1) {
                Map<Direction, BlockState> states = new HashMap<>();
                for (Map.Entry<Direction, MergeData> entry : mergeBlockEntity.mergingBlocks.entrySet()) {
                    states.put(entry.getKey(), entry.getValue().getState());
                }
                newState = merge.pl$doMultiMerge(level, blockPos, states, initialState);
            } else {
                for (Map.Entry<Direction, MergeData> entry : mergeBlockEntity.mergingBlocks.entrySet()) {
                    newState = merge.pl$doMerge(entry.getValue().getState(), level, blockPos, initialState, entry.getKey());
                    break;
                }
            }
            if (newState == null) {
                newState = Blocks.AIR.defaultBlockState();
            }
            BlockState blockState2 = Block.updateFromNeighbourShapes(newState, level, blockPos);
            if (blockState2.isAir()) {
                level.setBlock(blockPos, newState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
                Block.updateOrDestroy(newState, blockState2, level, blockPos, Block.UPDATE_ALL);
            } else {
                if (mergeBlockEntity.initialBlockEntity != null) {
                    mergeBlockEntity.initialBlockEntity.setLevel(level);
                    mergeBlockEntity.initialBlockEntity.setBlockState(blockState2);
                    mergeBlockEntity.initialBlockEntity.pl$beforeInitialFinalMerge(blockState2, mergeBlockEntity.mergingBlocks);
                    for (MergeData data : mergeBlockEntity.mergingBlocks.values()) {
                        if (data.hasBlockEntity()) {
                            data.getBlockEntity().pl$onAdvancedFinalMerge(mergeBlockEntity.initialBlockEntity);
                        }
                    }
                    mergeBlockEntity.initialBlockEntity.pl$afterInitialFinalMerge(blockState2, mergeBlockEntity.mergingBlocks);
                    Utils.setBlockWithEntity(level, blockPos, blockState2, mergeBlockEntity.initialBlockEntity, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);
                } else {
                    level.setBlock(blockPos, blockState2, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);
                }
                level.neighborChanged(blockPos, blockState2.getBlock(), blockPos);
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

    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        HolderGetter<Block> holderGetter = this.level != null ?
                this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.initialState = NbtUtils.readBlockState(holderGetter, compoundTag.getCompound("state"));
        if (compoundTag.contains("be", Tag.TAG_COMPOUND)) {
            EntityBlock movedBlock = (EntityBlock)this.initialState.getBlock();
            this.initialBlockEntity = movedBlock.newBlockEntity(BlockPos.ZERO, this.initialState);
            this.initialBlockEntity.load(compoundTag.getCompound("be"));
        }
        for (Direction dir : Direction.values()) {
            if (compoundTag.contains("dir" + dir.ordinal(), Tag.TAG_COMPOUND)) {
                CompoundTag tag = compoundTag.getCompound("dir" + dir.ordinal());
                mergingBlocks.put(dir, MergeData.loadNbt(holderGetter, tag));
            }
        }
    }

    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.put("state", NbtUtils.writeBlockState(initialState));
        if (this.initialBlockEntity != null) {
            compoundTag.put("be", this.initialBlockEntity.saveWithoutMetadata());
        }
        for (Map.Entry<Direction, MergeData> entry : mergingBlocks.entrySet()) {
            compoundTag.put("dir" + entry.getKey().ordinal(), MergeData.writeNbt(entry.getValue()));
        }
    }

    public static class MergeData {

        private final BlockState state;
        private final @Nullable BlockEntity be;
        private float progress;
        private float lastProgress;
        private float speed = 1F;

        public MergeData(BlockState state) {
            this(null, state);
        }

        public MergeData(@Nullable BlockEntity blockEntity, BlockState state) {
            this.state = state;
            this.be = blockEntity;
        }

        public boolean hasBlockEntity() {
            return be != null;
        }

        public BlockEntity getBlockEntity() {
            return be;
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

        public void onMovingTick(Level level, BlockPos toPos, Direction dir) {
            if (this.state.getBlock() instanceof MovingTickable tickable) {
                tickable.pl$movingTick(level, this.state, toPos, dir, this.lastProgress, this.speed, true);
            }
            if (this.be instanceof MovingTickable tickable) {
                tickable.pl$movingTick(level, this.state, toPos, dir, this.lastProgress, this.speed, true);
            }
        }

        public static CompoundTag writeNbt(MergeData data) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("state", NbtUtils.writeBlockState(data.getState()));
            if (data.hasBlockEntity()) {
                compoundTag.put("be", data.getBlockEntity().saveWithoutMetadata());
            }
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
            BlockEntity entity;
            if (compoundTag.contains("be", Tag.TAG_COMPOUND)) {
                EntityBlock movedBlock = (EntityBlock)state.getBlock();
                entity = movedBlock.newBlockEntity(BlockPos.ZERO, state);
                entity.load(compoundTag.getCompound("be"));
            } else {
                entity = null;
            }
            MergeData data = new MergeData(entity, state);
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
