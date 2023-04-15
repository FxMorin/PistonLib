package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public interface StructureGroup {

    default boolean hasInitialized() {
        return false;
    }

    void add(BasicMovingBlockEntity blockEntity);

    void add(int index, BasicMovingBlockEntity blockEntity);

    void remove(BasicMovingBlockEntity blockEntity);

    void remove(int index);

    BasicMovingBlockEntity get(int index);

    int size();

    BlockState getState(BlockPos blockPos);

    //
    // Grouped methods
    //

    /**
     * Calls a consumer against all group children including the controller
     */
    void forEach(Consumer<BasicMovingBlockEntity> action);

    /**
     * Calls a consumer against all group children except the controller
     */
    void forNonControllers(Consumer<BasicMovingBlockEntity> action);

    void saveAdditional(CompoundTag nbt);

    static ServerStructureGroup create(Level level) {
        if (level.isClientSide) {
            return new ClientStructureGroup(); // Holds rendering cache
        }
        return new ServerStructureGroup();
    }
}
