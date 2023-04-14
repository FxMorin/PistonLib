package ca.fxco.pistonlib.pistonLogic.structureGroups;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlockEntity;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
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
            } else {
                System.out.println("Failed to find structure block at: " + blockPos + " - " + be);
            }
        }
    }

    // This is what data compression looks like xD
    public void saveAdditional(CompoundTag nbt) {
        if (blockEntities.size() == 0) {
            // This happens when a player joins the server and loads the chunks.
            // Resulting in the player not seeing the block entities. Not sure why
            //System.out.println("ServerStructureGroup.saveAdditional) This shouldn't be possible");
            //Arrays.asList(Thread.currentThread().getStackTrace()).forEach(System.out::println);
            return;
        }
        BasicMovingBlockEntity controller = blockEntities.get(0);
        BlockPos basePos = controller.getBlockPos();
        int storeSize = blockEntities.size() - 1;
        int pushLimit = controller.getFamily().getPushLimit();
        if (pushLimit <= 126) { // relative block positions fit within bytes
            byte[] positions = new byte[storeSize * 3];
            int bytePos = 0;
            for (int i = 0; i < storeSize; i++) {
                BlockPos pos = blockEntities.get(i + 1).getBlockPos();
                positions[bytePos++] = (byte)(pos.getX() - basePos.getX());
                positions[bytePos++] = (byte)(pos.getY() - basePos.getY());
                positions[bytePos++] = (byte)(pos.getZ() - basePos.getZ());
            }
            nbt.putByteArray("controller", positions);
        } else if (pushLimit <= 32766) { // Fits into short
            ListTag positions = new ListTag();
            int shortPos = 0;
            for (int i = 0; i < storeSize; i++) {
                BlockPos pos = blockEntities.get(i + 1).getBlockPos();
                positions.addTag(shortPos++, ShortTag.valueOf((short)(pos.getX() - basePos.getX())));
                positions.addTag(shortPos++, ShortTag.valueOf((short)(pos.getY() - basePos.getY())));
                positions.addTag(shortPos++, ShortTag.valueOf((short)(pos.getZ() - basePos.getZ())));
            }
            nbt.put("controller", positions);
        } else { // just use ints
            int[] positions = new int[storeSize * 3];
            int intPos = 0;
            for (int i = 0; i < storeSize; i++) {
                BlockPos pos = blockEntities.get(i + 1).getBlockPos();
                positions[intPos++] = pos.getX() - basePos.getX();
                positions[intPos++] = pos.getY() - basePos.getY();
                positions[intPos++] = pos.getZ() - basePos.getZ();
            }
            nbt.putIntArray("controller", positions);
        }
    }
}
