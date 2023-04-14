package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This structure group holds a hashcode temporarily till the structure controller loads and sets the real structure group
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
        List<BlockPos> blockPosList = new ArrayList<>();
        if (pushLimit <= 126) { // relative block positions fit within bytes
            byte[] positions = nbt.getByteArray("controller");
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        basePos.getX() + positions[bytePos++],
                        basePos.getY() + positions[bytePos++],
                        basePos.getZ() + positions[bytePos++]
                ));
            }
        } else if (pushLimit <= 32766) { // Fits into short
            ListTag positions = nbt.getList("controller", Tag.TAG_SHORT);
            int shortPos = 0;
            for (int i = 0; i < positions.size(); i++) {
                blockPosList.add(new BlockPos(
                        basePos.getX() + positions.getShort(shortPos++),
                        basePos.getY() + positions.getShort(shortPos++),
                        basePos.getZ() + positions.getShort(shortPos++)
                ));
            }
        } else { // just use ints
            int[] positions = nbt.getIntArray("controller");
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        basePos.getX() + positions[bytePos++],
                        basePos.getY() + positions[bytePos++],
                        basePos.getZ() + positions[bytePos++]
                ));
            }
        }
    }
}
