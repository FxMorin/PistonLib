package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import ca.fxco.pistonlib.helpers.NbtUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This structure group temporarily holds a blockPos list till the block entity ticks for the first time and sets the real structure group
 */
@AllArgsConstructor
public class LoadingStructureGroup implements StructureGroup {

    @Getter
    private final List<BlockPos> blockPosList = new ArrayList<>();

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
    public void saveAdditional(CompoundTag nbt) {}

    public void onLoad(CompoundTag nbt, BlockPos basePos, int pushLimit) {
        if (!nbt.contains("controller")) {
            return;
        }

        blockPosList.addAll(NbtUtils.loadCompactRelativeBlockPosList(nbt, "controller", basePos, pushLimit));
    }
}
