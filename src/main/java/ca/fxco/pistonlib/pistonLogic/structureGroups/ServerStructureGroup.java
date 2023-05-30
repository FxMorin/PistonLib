package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.api.pistonlib.pistonLogic.structure.StructureGroup;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.helpers.NbtUtils;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Consumer;

@NoArgsConstructor
public class ServerStructureGroup implements StructureGroup {

    // The order they are added to the group is the order they should run in
    private final List<BasicMovingBlockEntity> blockEntities = new ArrayList<>();

    @Override
    public boolean hasInitialized() {
        return true;
    }

    @Override
    public void add(BasicMovingBlockEntity blockEntity) {
        this.blockEntities.add(blockEntity);
    }

    @Override
    public void add(int index, BasicMovingBlockEntity blockEntity) {
        this.blockEntities.add(index, blockEntity);
    }

    @Override
    public void remove(BasicMovingBlockEntity blockEntity) {
        this.blockEntities.remove(blockEntity);
    }

    @Override
    public void remove(int index) {
        this.blockEntities.remove(index);
    }

    @Override
    public BasicMovingBlockEntity get(int index) {
        return blockEntities.get(index);
    }

    @Override
    public int size() {
        return blockEntities.size();
    }

    @Override
    public BlockState getState(BlockPos blockPos) {
        throw new UnsupportedOperationException("This can only be called from the client!");
    }

    //
    // Grouped methods
    //

    /**
     * Calls a consumer against all group children including the controller
     */
    @Override
    public void forEach(Consumer<BasicMovingBlockEntity> action) {
        blockEntities.forEach(action);
    }

    /**
     * Calls a consumer against all group children except the controller
     */
    @Override
    public void forNonControllers(Consumer<BasicMovingBlockEntity> action) {
        for (int i = 1; i < blockEntities.size(); i++) {
            action.accept(blockEntities.get(i));
        }
    }

    //
    // Saving / Loading
    //

    // Attempt to find other block entities from your structure
    public void load(Level level, List<BlockPos> blockPosList) {
        for (BlockPos blockPos : blockPosList) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (be instanceof BasicMovingBlockEntity bmbe) {
                this.blockEntities.add(bmbe);
                bmbe.setStructureGroup(this);
            }
        }
    }

    public void saveAdditional(CompoundTag nbt) {
        blockEntities.removeIf(be -> be.isRemoved() || !be.hasLevel());
        if (blockEntities.size() == 0) {
            return;
        }
        BasicMovingBlockEntity controller = blockEntities.get(0);
        NbtUtils.saveCompactRelativeBlockPosList(
                nbt,
                "controller",
                controller.getBlockPos(),
                i -> blockEntities.get(i + 1).getBlockPos(),
                blockEntities.size() - 1,
                controller.getFamily().getPushLimit()
        );
    }
}
