package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StructureGroup {

    // The order they are added to the group is the order they should run in
    private final List<BasicMovingBlockEntity> blockEntities = new ArrayList<>();

    public void add(BasicMovingBlockEntity blockEntity) {
        this.blockEntities.add(blockEntity);
    }

    public void add(int index, BasicMovingBlockEntity blockEntity) {
        this.blockEntities.add(index, blockEntity);
    }

    public void remove(BasicMovingBlockEntity blockEntity) {
        this.blockEntities.remove(blockEntity);
    }

    public void remove(int index) {
        this.blockEntities.remove(index);
    }

    public BasicMovingBlockEntity get(int index) {
        return blockEntities.get(index);
    }

    public int size() {
        return blockEntities.size();
    }

    public BlockState getState(BlockPos blockPos) {
        throw new UnsupportedOperationException("This can only be called from the client!");
    }

    //
    // Grouped methods
    //

    /**
     * Calls a consumer against all group children including the controller
     */
    public void forEach(Consumer<BasicMovingBlockEntity> action) {
        blockEntities.forEach(action);
    }

    /**
     * Calls a consumer against all group children except the controller
     */
    public void forNonControllers(Consumer<BasicMovingBlockEntity> action) {
        for (int i = 1; i < blockEntities.size(); i++) {
            action.accept(blockEntities.get(i));
        }
    }

    public static StructureGroup create(Level level) {
        if (level.isClientSide) {
            return new ClientStructureGroup(); // Holds rendering cache
        } else {
            return new StructureGroup();
        }
    }
}
