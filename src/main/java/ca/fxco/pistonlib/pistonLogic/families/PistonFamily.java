package ca.fxco.pistonlib.pistonLogic.families;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModPistonFamilies;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class PistonFamily {

    private final PistonBehavior behavior;

    protected Map<PistonType, Block> base = new EnumMap<>(PistonType.class);
    @Nullable
    protected Block arm;
    protected Block head;
    protected Block moving;
    protected BlockEntityType<? extends BasicMovingBlockEntity> movingBlockEntityType;
    protected BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> movingBlockEntityFactory;

    public PistonFamily(PistonBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public String toString() {
        return "PistonFamily{" + ModPistonFamilies.getId(this) + "}";
    }

    public boolean hasCustomTextures() {
        return true; // Handled in DataGenPistonFamily
    }

    // Use this method to implement custom block types in families
    public boolean hasCustomBlocks(Block block) {
        return false;
    }

    public Map<PistonType, Block> getBases() {
        return Collections.unmodifiableMap(this.base);
    }

    public @Nullable Block getBase(PistonType type) {
        return this.base.get(type);
    }

    /**
     * @return an arbitrary base block
     */
    public Block getBase() {
        return this.base.values().iterator().next();
    }

    public @Nullable Block getArm() {
        return this.arm;
    }

    public Block getHead() {
        return this.head;
    }

    public Block getMoving() {
        return moving;
    }

    public BlockEntityType<? extends BasicMovingBlockEntity> getMovingBlockEntityType() {
        return this.movingBlockEntityType;
    }

    public BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> getMovingBlockEntityFactory() {
        return this.movingBlockEntityFactory;
    }

    public BasicMovingBlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                                       BlockEntity movedBlockEntity, Direction facing,
                                                       boolean extending, boolean isSourcePiston) {
        return this.movingBlockEntityFactory
            .create(this, null, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
    }

    public BasicMovingBlockEntity newMovingBlockEntity(StructureGroup structureGroup, BlockPos pos, BlockState state,
                                                       BlockState movedState, BlockEntity movedBlockEntity,
                                                       Direction facing, boolean extending, boolean isSourcePiston) {
        return this.movingBlockEntityFactory
                .create(this, structureGroup, pos, state, movedState, movedBlockEntity, facing,
                        extending, isSourcePiston);
    }

    public void setBase(Block base) {
        if (ModPistonFamilies.requireNotLocked()) {
            this.base.put(((BasicPistonBaseBlock)base).type, base);
        }
    }

    public void setArm(Block arm) {
        if (ModPistonFamilies.requireNotLocked()) {
            this.arm = arm;
        }
    }

    public void setHead(Block head) {
        if (ModPistonFamilies.requireNotLocked()) {
            this.head = head;
        }
    }

    public void setMoving(Block moving) {
        if (ModPistonFamilies.requireNotLocked()) {
            this.moving = moving;
        }
    }

    public void setMovingBlockEntity(
        BlockEntityType<? extends BasicMovingBlockEntity> type,
        BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> factory
    ) {
        if (ModPistonFamilies.requireNotLocked()) {
            this.movingBlockEntityType = type;
            this.movingBlockEntityFactory = factory;
        }
    }

    public boolean isVerySticky() {
        return this.behavior.verySticky;
    }

    public boolean isFrontPowered() {
        return this.behavior.frontPowered;
    }

    public boolean isTranslocation() {
        return this.behavior.translocation;
    }

    public boolean isSlippery() {
        return this.behavior.slippery;
    }

    public boolean isQuasi() {
        return this.behavior.quasi;
    }

    public int getPushLimit() {
        return this.behavior.pushLimit;
    }

    public float getExtendingSpeed() {
        return this.behavior.extendingSpeed;
    }

    public float getRetractingSpeed() {
        return this.behavior.retractingSpeed;
    }

    public boolean canRetractOnExtending() {
        return this.behavior.canRetractOnExtending;
    }

    public boolean canExtendOnRetracting() {
        return this.behavior.canExtendOnRetracting;
    }


    public static PistonFamily of(PistonBehavior behavior) {
        if (PistonLib.DATAGEN_ACTIVE) {
            return new DataGenPistonFamily(behavior);
        }
        return new PistonFamily(behavior);
    }

    public static PistonFamily of(PistonBehavior behavior, boolean hasCustomTextures) {
        if (PistonLib.DATAGEN_ACTIVE) {
            return new DataGenPistonFamily(behavior, hasCustomTextures);
        }
        return new PistonFamily(behavior);
    }
}
