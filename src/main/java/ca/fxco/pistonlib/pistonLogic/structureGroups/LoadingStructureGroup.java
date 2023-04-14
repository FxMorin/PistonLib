package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * This structure group holds a hashcode temporarily till the structure controller loads and sets the real structure group
 */
@AllArgsConstructor
public class LoadingStructureGroup implements StructureGroup {

    private final int structureHash;

    @Override
    public void add(BasicMovingBlockEntity blockEntity) {}

    @Override
    public void add(int index, BasicMovingBlockEntity blockEntity) {}

    @Override
    public void remove(BasicMovingBlockEntity blockEntity) {}

    @Override
    public void remove(int index) {}

    @Override
    public BasicMovingBlockEntity get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public BlockState getState(BlockPos blockPos) {
        return null;
    }

    @Override
    public void forEach(Consumer<BasicMovingBlockEntity> action) {}

    @Override
    public void forNonControllers(Consumer<BasicMovingBlockEntity> action) {}

    @Override
    public int hashCode() {
        return this.structureHash;
    }
}
