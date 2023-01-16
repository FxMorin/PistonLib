package ca.fxco.pistonlib.pistonLogic.families;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

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

    private final boolean customTextures;

    protected Map<PistonType, Block> base = new EnumMap<>(PistonType.class);
    @Nullable
    protected Block arm;
    protected Block head;
    protected Block moving;
    protected BlockEntityType<? extends BasicMovingBlockEntity> movingBlockEntityType;
    protected BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> movingBlockEntityFactory;

    public PistonFamily() {
        this(true);
    }

    PistonFamily(boolean hasCustomTextures) {
        this.customTextures = hasCustomTextures;
    }

    @Override
    public String toString() {
        return "PistonFamily{" + PistonFamilies.getId(this) + "}";
    }

    public boolean hasCustomTextures() {
        return this.customTextures;
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

    public BasicMovingBlockEntity newMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                                       BlockEntity movedBlockEntity, Direction facing,
                                                       boolean extending, boolean isSourcePiston) {
        return this.movingBlockEntityFactory
            .create(this, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
    }

    public void setBase(Block base) {
        if (PistonFamilies.requireNotLocked()) {
            this.base.put(((BasicPistonBaseBlock)base).type, base);
        }
    }

    public void setArm(Block arm) {
        if (PistonFamilies.requireNotLocked()) {
            this.arm = arm;
        }
    }

    public void setHead(Block head) {
        if (PistonFamilies.requireNotLocked()) {
            this.head = head;
        }
    }

    public void setMoving(Block moving) {
        if (PistonFamilies.requireNotLocked()) {
            this.moving = moving;
        }
    }

    public void setMovingBlockEntity(
        BlockEntityType<? extends BasicMovingBlockEntity> type,
        BasicMovingBlockEntity.Factory<? extends BasicMovingBlockEntity> factory
    ) {
        if (PistonFamilies.requireNotLocked()) {
            this.movingBlockEntityType = type;
            this.movingBlockEntityFactory = factory;
        }
    }
}
