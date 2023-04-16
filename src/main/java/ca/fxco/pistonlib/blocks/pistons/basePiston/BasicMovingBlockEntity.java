package ca.fxco.pistonlib.blocks.pistons.basePiston;

import java.util.*;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.base.ModBlocks;
import ca.fxco.pistonlib.base.ModPistonFamilies;
import ca.fxco.pistonlib.helpers.IonicReference;
import ca.fxco.pistonlib.impl.BlockEntityPostLoad;
import ca.fxco.pistonlib.mixin.accessors.BlockEntityAccessor;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import ca.fxco.pistonlib.pistonLogic.structureGroups.LoadingStructureGroup;
import ca.fxco.pistonlib.pistonLogic.structureGroups.ServerStructureGroup;
import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BasicMovingBlockEntity extends PistonMovingBlockEntity implements BlockEntityPostLoad {

    protected final PistonType type;

    @Getter
    private PistonFamily family;

    @Setter
    @Getter
    private @Nullable StructureGroup structureGroup;
    private boolean isGroupController;

    public BasicMovingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        this.type = state.getValue(BasicMovingBlock.TYPE);

        this.structureGroup = null;
        this.isGroupController = false;
    }

    public BasicMovingBlockEntity(PistonFamily family, @Nullable StructureGroup structureGroup,
                                  BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity,
                                  Direction facing, boolean extending, boolean isSourcePiston) {
        super(pos, state, movedState, facing, extending, isSourcePiston);

        this.type = state.getValue(BasicMovingBlock.TYPE);

        this.setFamily(family);

        if (PistonLibConfig.pistonStructureGrouping) {
            this.structureGroup = structureGroup;
            if (this.structureGroup != null) {
                this.isGroupController = this.structureGroup.size() == 0;
                this.structureGroup.add(this); // Add self to the structure group
            } else {
                isGroupController = false;
            }
        } else {
            this.structureGroup = null;
            this.isGroupController = false;
        }
    }

    private void setFamily(PistonFamily family) {
        this.family = family;
        ((BlockEntityAccessor)this).setType(this.family.getMovingBlockEntityType());
    }

    public boolean hasControl() {
        return this.structureGroup == null || this.isGroupController;
    }

    public float speed() {
        return 1.0F;
    }

    protected double movementMargin() {
        return 0.01D * speed();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public float getExtendedProgress(float progress) {
        return this.extending ? progress - 1.0F : 1.0F - progress;
    }

    /**
     * Replaced by {@link #getStateForMovingEntities()}
     */
    @Override
    @Deprecated
    public BlockState getCollisionRelatedBlockState() {
        // Removed setting the type since this is currently only used for collision shape
        return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof BasicPistonBaseBlock ?
            ModBlocks.BASIC_PISTON_HEAD.defaultBlockState()
                .setValue(BasicPistonHeadBlock.SHORT, this.progress > 0.25F)
                .setValue(BasicPistonHeadBlock.FACING, this.movedState.getValue(BasicPistonBaseBlock.FACING)) :
            this.movedState;
    }

    protected BlockState getStateForMovingEntities() {
        return this.getCollisionRelatedBlockState();
    }

    protected void moveCollidedEntities(float nextProgress) {
        if (this.isGroupController) {
            VoxelShape blockShape = this.getStateForMovingEntities().getCollisionShape(this.level, this.worldPosition);
            Direction moveDir = this.getMovementDirection();
            double deltaProgress = nextProgress - this.progress;
            Map<BlockEntity, Pair<List<AABB>, AABB>> blockShapes = new HashMap<>();
            IonicReference<AABB> combinedBounds;
            if (!blockShape.isEmpty()) {
                AABB initialBounds = moveByPositionAndProgress(this.worldPosition, blockShape.bounds());
                initialBounds = PistonMath.getMovementArea(initialBounds, moveDir, deltaProgress).minmax(initialBounds);
                combinedBounds = new IonicReference<>(initialBounds);
                blockShapes.put(this, Pair.of(blockShape.toAabbs(), initialBounds));
            } else {
                combinedBounds = new IonicReference<>(new AABB(0,0,0,0,0,0));
            }

            this.structureGroup.forNonControllers(be -> {
                VoxelShape blockShape2 = be.getStateForMovingEntities().getCollisionShape(be.level, be.worldPosition);
                if (blockShape2.isEmpty()) {
                    return;
                }
                AABB bound = moveByPositionAndProgress(be.worldPosition, blockShape2.bounds());
                bound = PistonMath.getMovementArea(bound, moveDir, deltaProgress).minmax(bound);
                combinedBounds.set(combinedBounds.get().minmax(bound));
                blockShapes.put(be, Pair.of(blockShape2.toAabbs(), bound));
            });
            List<Entity> entities = this.level.getEntities(null, combinedBounds.get());
            entities.removeIf(entity -> entity.getPistonPushReaction() == PushReaction.IGNORE);
            if (entities.isEmpty()) {
                return;
            }
            this.structureGroup.forEach(be -> {
                List<Entity> affectedEntities = new ArrayList<>();
                Pair<List<AABB>, AABB> pair = blockShapes.get(be);
                if (pair == null) {
                    return;
                }
                AABB blockBounds = pair.second();
                for (Entity entity : entities) {
                    if (entity.getBoundingBox().intersects(blockBounds)) {
                        affectedEntities.add(entity);
                    }
                }
                if (!affectedEntities.isEmpty()) {
                    be.moveCollidedEntities(moveDir, deltaProgress, blockShapes.get(be).first(), affectedEntities);
                }
            });
        } else if (this.structureGroup == null) {
            VoxelShape blockShape = this.getStateForMovingEntities().getCollisionShape(this.level, this.worldPosition);
            Direction moveDir = this.getMovementDirection();
            double deltaProgress = nextProgress - this.progress;
            if (blockShape.isEmpty()) {
                return;
            }
            AABB totalBlockBounds = moveByPositionAndProgress(this.worldPosition, blockShape.bounds());
            AABB totalMovementArea = PistonMath.getMovementArea(totalBlockBounds, moveDir, deltaProgress).minmax(totalBlockBounds);
            List<Entity> entities = this.level.getEntities(null, totalMovementArea);
            entities.removeIf(entity -> entity.getPistonPushReaction() == PushReaction.IGNORE);
            if (entities.isEmpty()) {
                return;
            }
            moveCollidedEntities(moveDir, deltaProgress, blockShape.toAabbs(), entities);
        }
    }

    protected void moveCollidedEntities(Direction moveDir, double deltaProgress, List<AABB> blockAabbs, List<Entity> entities) {
        boolean shouldBounceEntities = this.movedState.is(Blocks.SLIME_BLOCK);
        float speed = this.speed();

        for (Entity entity : entities) {
            if (shouldBounceEntities) {
                if (entity instanceof ServerPlayer) {
                    continue;
                }

                Vec3 velocity = entity.getDeltaMovement();
                double velocityX = velocity.x;
                double velocityY = velocity.y;
                double velocityZ = velocity.z;

                switch (moveDir.getAxis()) {
                    case X -> velocityX = moveDir.getStepX() * speed;
                    case Y -> velocityY = moveDir.getStepY() * speed;
                    case Z -> velocityZ = moveDir.getStepZ() * speed;
                }

                entity.setDeltaMovement(velocityX, velocityY, velocityZ);
            }

            double movement = 0.0D;

            for (AABB blockAabb : blockAabbs) {
                AABB blockBounds = moveByPositionAndProgress(this.worldPosition, blockAabb);
                AABB movementArea = PistonMath.getMovementArea(blockBounds, moveDir, deltaProgress);
                AABB entityAabb = entity.getBoundingBox();

                if (movementArea.intersects(entityAabb)) {
                    movement = Math.max(movement, getMovement(movementArea, moveDir, entityAabb));

                    if (movement >= deltaProgress) {
                        break;
                    }
                }
            }
            if (movement <= 0.0D) {
                continue;
            }

            moveEntity(moveDir, entity, Math.min(movement, deltaProgress) + this.movementMargin(), moveDir);

            if (!this.extending && this.isSourcePiston) {
                fixEntityWithinPistonBase(entity, moveDir, deltaProgress);
            }
        }
    }

    protected void fixEntityWithinPistonBase(Entity entity, Direction moveDir, double deltaProgress) {
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
    }

    protected void moveStuckEntities(float nextProgress) {
        if (isStickyForEntities()) {
            Direction moveDir = getMovementDirection();

            if (moveDir.getAxis().isHorizontal()) {
                double maxY = this.movedState.getCollisionShape(this.level, this.worldPosition).max(Direction.Axis.Y);
                AABB movementArea = moveByPositionAndProgress(this.worldPosition, new AABB(0.0D, maxY, 0.0D, 1.0D, 1.5D, 1.0D));
                double deltaProgress = nextProgress - this.progress;

                List<Entity> entities = this.level.getEntities((Entity)null, movementArea, entity -> matchesStickyCriterea(movementArea, entity));

                for (Entity entity : entities) {
                    moveEntity(moveDir, entity, deltaProgress, moveDir);
                }
            }
        }
    }

    protected boolean matchesStickyCriterea(AABB movementArea, Entity entity) {
        return entity.getPistonPushReaction() == PushReaction.NORMAL && entity.isOnGround() &&
            entity.getX() >= movementArea.minX &&
            entity.getX() <= movementArea.maxX &&
            entity.getZ() >= movementArea.minZ &&
            entity.getZ() <= movementArea.maxZ;
    }

    protected double getMovement(AABB movementArea, Direction moveDir, AABB entityAabb) {
        return switch (moveDir) {
            default -> movementArea.maxY - entityAabb.minY;
            case EAST -> movementArea.maxX - entityAabb.minX;
            case WEST -> entityAabb.maxX - movementArea.minX;
            case DOWN -> entityAabb.maxY - movementArea.minY;
            case SOUTH -> movementArea.maxZ - entityAabb.minZ;
            case NORTH -> entityAabb.maxZ - movementArea.minZ;
        };
    }

    protected AABB moveByPositionAndProgress(BlockPos pos, AABB aabb) {
        double extendedProgress = getExtendedProgress(progress);

        return aabb.move(
            pos.getX() + extendedProgress * direction.getStepX(),
            pos.getY() + extendedProgress * direction.getStepY(),
            pos.getZ() + extendedProgress * direction.getStepZ()
        );
    }

    protected void moveEntity(Direction noclipDir, Entity entity, double amount, Direction moveDir) {
        NOCLIP.set(noclipDir);
        entity.move(MoverType.PISTON, new Vec3(
            amount * moveDir.getStepX(),
            amount * moveDir.getStepY(),
            amount * moveDir.getStepZ()
        ));
        NOCLIP.set(null);
    }

    @Override
    public boolean isStickyForEntities() {
        return this.movedState.is(Blocks.HONEY_BLOCK);
    }

    @Override
    public Direction getMovementDirection() {
        return this.extending ? this.direction : this.direction.getOpposite();
    }

    @Override
    public BlockState getMovedState() {
        return this.movedState;
    }

    public void finalTickStuckNeighbors(Map<Direction, StickyType> stickyTypes) {
        for (Map.Entry<Direction, StickyType> entry : stickyTypes.entrySet()) {
            StickyType stickyType = entry.getValue();

            if (stickyType.ordinal() < StickyType.STRONG.ordinal()) { // only strong or fused
                continue;
            }

            Direction dir = entry.getKey();
            BlockPos neighborPos = this.worldPosition.relative(dir);
            BlockState neighborState = this.level.getBlockState(neighborPos);

            if (neighborState.is(this.family.getMoving())) {
                BlockEntity blockEntity = this.level.getBlockEntity(neighborPos);

                if (blockEntity instanceof BasicMovingBlockEntity mbe) {
                    if (this.getMovementDirection() == mbe.getMovementDirection() && this.progressO == mbe.progress) {
                        // Maybe do a stick test?
                        mbe.finalTick();
                    }
                }
            }
        }
    }

    @Override
    public void finalTick() {
        if (this.isGroupController && PistonLibConfig.combinedBlockDropping) {
            this.structureGroup.forEach(t -> t.finalTick(true, true));
            return;
        }
        finalTick(false, true);
    }

    public void finalTick(boolean skipStickiness, boolean removeSource) { // Only used by `finalTickStuckNeighbors` & structure groups
        if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide())) {

            this.progressO = this.progress;
            this.progress = 1.0F;

            this.finishMovement(removeSource);

            if (!skipStickiness && PistonLibConfig.strongBlockDropping) {
                ConfigurablePistonStickiness stick = (ConfigurablePistonStickiness) this.movedState.getBlock();

                if (stick.usesConfigurablePistonStickiness() && stick.isSticky(this.movedState)) {
                    this.finalTickStuckNeighbors(stick.stickySides(this.movedState));
                }
            }

            this.progressO = 1.0F;
        }
    }

    public void tick() {
        tickStart();
        tickMovement();
    }

    /**
     * @return true if it finished movement
     */
    public void tickStart() {
        this.lastTicked = this.level.getGameTime();
        this.progressO = this.progress;
        if (this.progressO >= 1.0F) {
            if (this.level.isClientSide() && this.deathTicks < 5) {
                this.deathTicks++;
            } else {
                this.finishMovement(false);
            }
        }
        if (this.isGroupController) {
            this.structureGroup.forNonControllers(BasicMovingBlockEntity::tickStart);
        }
    }

    public void tickMovement() {
        if (this.progressO < 1.0F) {
            float nextProgress = this.progress + 0.5F * this.speed();

            this.moveCollidedEntities(nextProgress);
            this.moveStuckEntities(nextProgress);

            this.progress = nextProgress;
            if (this.progress >= 1.0F) {
                this.progress = 1.0F;
            }
            if (this.isGroupController) {
                this.structureGroup.forNonControllers(t -> {
                    t.progress = this.progress;
                });
            }
        }
    }

    protected void finishMovement(boolean removeSource) {
        this.level.removeBlockEntity(this.worldPosition);
        this.setRemoved();

        if (this.level.getBlockState(this.worldPosition).is(this.family.getMoving())) {
            this.placeAndUpdateMovedBlock(removeSource);
        }
    }

    protected void placeAndUpdateMovedBlock(boolean removeSource) {
        boolean setAir = removeSource && this.isSourcePiston;
        if (!setAir && !this.placeMovedBlock()) {
            return;
        }

        BlockState updatedState = setAir ? Blocks.AIR.defaultBlockState() :
                Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);

        if (updatedState == this.movedState) { // If it doesn't change, add updates manually
            this.level.updateNeighborsAt(this.worldPosition, updatedState.getBlock());

            updatedState.updateNeighbourShapes(this.level, this.worldPosition, Block.UPDATE_ALL);
            updatedState.updateIndirectNeighbourShapes(this.level, this.worldPosition, Block.UPDATE_ALL);
        } else {
            this.level.setBlock(this.worldPosition, updatedState, Block.UPDATE_ALL);
        }

        this.level.neighborChanged(this.worldPosition, updatedState.getBlock(), this.worldPosition);
    }

    protected boolean placeMovedBlock() {
        return this.level.setBlock(this.worldPosition, this.movedState,
            Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
    }

    @Override
    public boolean shouldPostLoad() {
        return this.isGroupController && this.structureGroup != null && !this.structureGroup.hasInitialized();
    }

    @Override
    public void onPostLoad() {
        if (this.structureGroup != null && this.structureGroup instanceof LoadingStructureGroup loadingStructureGroup) {
            ServerStructureGroup controllerStructure = StructureGroup.create(this.level);
            controllerStructure.load(this.level, loadingStructureGroup.getBlockPosList());
            this.structureGroup = controllerStructure;
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        this.setFamily(ModPistonFamilies.get(new ResourceLocation(nbt.getString("family"))));
        this.movedState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("blockState"));
        this.direction = Direction.from3DDataValue(nbt.getInt("facing"));
        this.progress = nbt.getFloat("progress");
        if (PistonLibConfig.pistonProgressFix) {
            this.progressO = nbt.contains("progressO") ? nbt.getFloat("progressO") : this.progress;
        } else {
            this.progressO = this.progress;
        }
        this.extending = nbt.getBoolean("extending");
        this.isSourcePiston = nbt.getBoolean("source");
        if (PistonLibConfig.pistonStructureGrouping && nbt.contains("controller")) {
            LoadingStructureGroup loadingStructureGroup = new LoadingStructureGroup();
            this.structureGroup = loadingStructureGroup;
            loadingStructureGroup.onLoad(nbt, this.worldPosition, this.family.getPushLimit());
            this.isGroupController = true;
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("family", ModPistonFamilies.getId(this.family).toString());
        nbt.put("blockState", NbtUtils.writeBlockState(this.movedState));
        nbt.putInt("facing", this.direction.get3DDataValue());
        if (PistonLibConfig.pistonProgressFix) {
            nbt.putFloat("progress", this.progress);
            nbt.putFloat("progressO", this.progressO);
        } else {
            nbt.putFloat("progress", this.progressO);
        }
        nbt.putBoolean("extending", this.extending);
        nbt.putBoolean("source", this.isSourcePiston);
        if (this.isGroupController && this.structureGroup != null && this.structureGroup.hasInitialized()) {
            this.structureGroup.saveAdditional(nbt);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockGetter level, BlockPos pos) {
        BlockState staticState = getStaticStateForCollisionShape();
        VoxelShape staticShape = staticState.getCollisionShape(level, pos);

        Direction noclipDir = NOCLIP.get();

        if (this.progress < 1.0F && noclipDir == this.getMovementDirection()) {
            return staticShape;
        }

        float extendedProgress = this.getExtendedProgress(this.progress);
        double dx = this.direction.getStepX() * extendedProgress;
        double dy = this.direction.getStepY() * extendedProgress;
        double dz = this.direction.getStepZ() * extendedProgress;

        BlockState movingState = getMovingStateForCollisionShape();
        VoxelShape movingShape = movingState.getCollisionShape(level, pos).move(dx, dy, dz);

        return Shapes.or(staticShape, movingShape);
    }

    protected BlockState getStaticStateForCollisionShape() {
        if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof BasicPistonBaseBlock) {
            return this.movedState.setValue(BasicPistonBaseBlock.EXTENDED, true);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    protected BlockState getMovingStateForCollisionShape() {
        if (this.isSourcePiston()) {
            return ModBlocks.BASIC_PISTON_HEAD.defaultBlockState()
                .setValue(PistonHeadBlock.FACING, this.direction)
                .setValue(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
        } else {
            return this.movedState;
        }
    }

    @Override
    public long getLastTicked() {
        return this.lastTicked;
    }

    @FunctionalInterface
    public interface Factory<T extends BasicMovingBlockEntity> {

        T create(PistonFamily family, StructureGroup structureGroup, BlockPos pos, BlockState state,
                 BlockState movedState, BlockEntity movedBlockEntity, Direction facing, boolean extending,
                 boolean isSourcePiston);

    }
}
