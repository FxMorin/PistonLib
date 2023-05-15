package ca.fxco.pistonlib.pistonLogic.families;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.pistonLogic.structureGroups.StructureGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
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

@RequiredArgsConstructor
public class PistonFamily {

    @Delegate(types=PistonBehavior.class, excludes=PistonBehavior.PistonBehaviorBuilder.class)
    private final PistonBehavior behavior;

    protected Map<PistonType, Block> base = new EnumMap<>(PistonType.class);
    @Getter
    protected @Nullable Block arm;
    @Getter
    protected Block head;
    @Getter
    protected Block moving;
    @Getter
    protected BlockEntityType<? extends BasicMovingBlockEntity> movingBlockEntityType;
    @Getter
    protected BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> movingBlockEntityFactory;

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
            this.base.put(((BasicPistonBaseBlock)base).getType(), base);
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

    public boolean hasCustomLength() {
        return this.behavior.getMinLength() != 0 || this.behavior.getMaxLength() != 1;
    }

    public static PistonFamily of(PistonBehavior behavior) {
        return PistonLib.DATAGEN_ACTIVE ? new DataGenPistonFamily(behavior, true) : new PistonFamily(behavior);
    }

    public static PistonFamily of(PistonBehavior behavior, boolean hasCustomTextures) {
        return PistonLib.DATAGEN_ACTIVE ? new DataGenPistonFamily(behavior, hasCustomTextures) : new PistonFamily(behavior);
    }
}
