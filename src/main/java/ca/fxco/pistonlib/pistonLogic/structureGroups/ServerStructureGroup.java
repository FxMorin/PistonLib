package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
@NoArgsConstructor
public class ServerStructureGroup implements StructureGroup {

    // The order they are added to the group is the order they should run in
    private final List<BasicMovingBlockEntity> blockEntities = new ArrayList<>();
    private int structureHash;

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

    @Override
    public int hashCode() {
        if (structureHash == 0) {
            structureHash = blockEntities.hashCode();
        }
        return structureHash;
    }

    // Attempt to find other block entities from your structure
    public void onLoad(Level level, BlockPos pos, int max) {
        Queue<BlockPos> queue = new LinkedList<>();
        List<BasicMovingBlockEntity> tempBlockEntities = new ArrayList<>(); // Could add them directly to the structure however I may want to sort them
        HashSet<BlockPos> visited = new HashSet<>();

        queue.add(pos);
        visited.add(pos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = new BlockPos(current.getX() + dir.getStepX(), current.getY() + dir.getStepY(), current.getZ() + dir.getStepZ());
                if (visited.contains(neighborPos)) {
                    continue;
                }
                visited.add(neighborPos);
                BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);
                if (neighborBlockEntity == null) {
                    continue;
                }
                if (neighborBlockEntity instanceof BasicMovingBlockEntity bmbe) {
                    StructureGroup structure = bmbe.getStructureGroup();
                    if (structure != null && !structure.hasInitialized() && structure.hashCode() == structureHash) {
                        bmbe.setStructureGroup(this);
                        tempBlockEntities.add(bmbe);
                        if (tempBlockEntities.size() == max) {
                            queue.clear();
                            continue;
                        }
                        queue.add(neighborPos);
                    }
                }
            }
        }
        this.blockEntities.addAll(tempBlockEntities);
    }
}
