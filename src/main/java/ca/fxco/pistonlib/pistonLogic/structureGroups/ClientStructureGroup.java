package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ClientStructureGroup extends ServerStructureGroup {

    private final Map<BlockPos, BlockState> fastStateLookup = new HashMap<>();

    @Override
    public void add(BasicMovingBlockEntity blockEntity) {
        super.add(blockEntity);
        fastStateLookup.put(blockEntity.getBlockPos().relative(blockEntity.getMovementDirection().getOpposite()), blockEntity.getMovedState());
    }

    @Override
    public void add(int index, BasicMovingBlockEntity blockEntity) {
        super.add(index, blockEntity);
        fastStateLookup.put(blockEntity.getBlockPos().relative(blockEntity.getMovementDirection().getOpposite()), blockEntity.getMovedState());
    }

    @Override
    public BlockState getState(BlockPos blockPos) {
        BlockState state = fastStateLookup.get(blockPos);
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
