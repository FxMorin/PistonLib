package ca.fxco.pistonlib.helpers;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

@UtilityClass
public class NbtUtils {

    //
    // Items
    //

    /**
     * Saves all items in the given {@link Container} to a {@link CompoundTag}
     *
     * @param compoundTag The {@link CompoundTag} to save the items to
     * @param container The {@link Container} to save the items from
     */
    public static void saveAllItems(CompoundTag compoundTag, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                compoundTag.put("" + i, container.getItem(i).save(new CompoundTag()));
            }
        }
    }

    /**
     * Loads all items in the given {@link Container} from a {@link CompoundTag}
     *
     * @param compoundTag The {@link CompoundTag} containing the items to load
     * @param container The {@link Container} to load the items into
     */
    public static void loadAllItems(CompoundTag compoundTag, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (compoundTag.contains("" + i, Tag.TAG_COMPOUND)) {
                container.setItem(i, ItemStack.of(compoundTag.getCompound("" + i)));
            } else {
                container.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    //
    // BlockPos
    //

    /**
     * Saves the given {@link BlockPos} to NBT
     *
     * @param nbt The {@link CompoundTag} to save to
     * @param id The identifier to save the position to within the {@link CompoundTag}
     * @param blockPos The {@link BlockPos} to save
     */
    public static void saveBlockPos(CompoundTag nbt, String id, BlockPos blockPos) {
        nbt.putIntArray(id, new int[] {
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        });
    }

    /**
     * Loads a {@link BlockPos} from NBT
     *
     * @param nbt The {@link CompoundTag} to load from
     * @param id The identifier for the block position in the {@link CompoundTag}
     * @return A {@link BlockPos}
     */
    public static BlockPos loadBlockPos(CompoundTag nbt, String id) {
        int[] pos = nbt.getIntArray(id);
        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    /**
     * Saves a list of {@link BlockPos} to NBT
     *
     * @param nbt The {@link CompoundTag} to save to
     * @param id The identifier to save the positions to within the {@link CompoundTag}
     * @param blockPosFunction A function that generates block positions to save
     * @param amt The number of {@link BlockPos} to save
     */
    public static void saveBlockPosList(CompoundTag nbt, String id, IntFunction<BlockPos> blockPosFunction, int amt) {
        int[] positions = new int[amt * 3];
        int intPos = 0;
        for (int i = 0; i < amt; i++) {
            BlockPos pos = blockPosFunction.apply(i);
            positions[intPos++] = pos.getX();
            positions[intPos++] = pos.getY();
            positions[intPos++] = pos.getZ();
        }
        nbt.putIntArray(id, positions);
    }

    /**
     * Loads a list of {@link BlockPos} from NBT
     *
     * @param nbt The {@link CompoundTag} to load from
     * @param id The identifier for the block position list in the {@link CompoundTag}
     * @return A List of {@link BlockPos}
     */
    public static List<BlockPos> loadBlockPosList(CompoundTag nbt, String id) {
        int[] positions = nbt.getIntArray(id);
        int bytePos = 0;
        List<BlockPos> blockPosList = new ArrayList<>();
        for (int i = 0; i < positions.length / 3; i++) {
            blockPosList.add(new BlockPos(
                    positions[bytePos++],
                    positions[bytePos++],
                    positions[bytePos++]
            ));
        }
        return blockPosList;
    }

    /**
     * Saves a list of {@link BlockPos} relative to a base position to NBT
     *
     * @param nbt The {@link CompoundTag} to save to
     * @param id The identifier to save the positions to within the {@link CompoundTag}
     * @param basePos The base position to use when calculating the relative positions
     * @param blockPosFunction The function to retrieve the {@link BlockPos} to save for each index
     * @param amt The amount of {@link BlockPos} to save
     */
    public static void saveRelativeBlockPosList(CompoundTag nbt, String id, BlockPos basePos,
                                                IntFunction<BlockPos> blockPosFunction, int amt) {
        int[] positions = new int[amt * 3];
        int intPos = 0;
        for (int i = 0; i < amt; i++) {
            BlockPos pos = blockPosFunction.apply(i);
            positions[intPos++] = pos.getX() - basePos.getX();
            positions[intPos++] = pos.getY() - basePos.getY();
            positions[intPos++] = pos.getZ() - basePos.getZ();
        }
        nbt.putIntArray(id, positions);
    }

    /**
     * Loads a list of {@link BlockPos} relative to a base position from NBT
     *
     * @param nbt The {@link CompoundTag} to load the block position list from
     * @param id The identifier for the block position list in the {@link CompoundTag}
     * @param basePos The base position to use when calculating the relative positions
     * @return A List of {@link BlockPos}
     */
    public static List<BlockPos> loadRelativeBlockPosList(CompoundTag nbt, String id, BlockPos basePos) {
        int[] positions = nbt.getIntArray(id);
        int bytePos = 0;
        List<BlockPos> blockPosList = new ArrayList<>();
        for (int i = 0; i < positions.length / 3; i++) {
            blockPosList.add(new BlockPos(
                    basePos.getX() + positions[bytePos++],
                    basePos.getY() + positions[bytePos++],
                    basePos.getZ() + positions[bytePos++]
            ));
        }
        return blockPosList;
    }

    /**
     * Saves a compact representation of a {@link BlockPos} to a {@link CompoundTag}, using the specified max value to determine
     * the most space-efficient format. If the max value is 126 or lower, the position is stored using three bytes,
     * one for each coordinate. Otherwise, the position is stored as an array of three integers.
     *
     * @param nbt The {@link CompoundTag} to save the compact {@link BlockPos} to
     * @param id The identifier to use when saving the compact {@link BlockPos} to the {@link CompoundTag}
     * @param blockPos The {@link BlockPos} to save in compact form
     * @param max The maximum value that any {@link BlockPos} can have
     */
    public static void saveCompactBlockPos(CompoundTag nbt, String id, BlockPos blockPos, int max) {
        if (max <= 126) { // block positions fit within 3 bytes
            nbt.putByteArray(id, new byte[] {
                    (byte) blockPos.getX(),
                    (byte) blockPos.getY(),
                    (byte)blockPos.getZ()
            });
        } else { // just use ints
            nbt.putIntArray(id, new int[] {
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ()
            });
        }
    }

    /**
     * Loads a {@link BlockPos} from NBT
     *
     * @param nbt The {@link CompoundTag} to load the compact {@link BlockPos} from
     * @param id The identifier for the block position in the {@link CompoundTag}
     * @param max The maximum value that any coordinate in the {@link BlockPos} can have
     * @return A {@link BlockPos}
     */
    public static BlockPos loadCompactBlockPos(CompoundTag nbt, String id, int max) {
        if (max <= 126) { // block positions fit within 3 bytes
            byte[] pos = nbt.getByteArray(id);
            return new BlockPos(pos[0], pos[1], pos[2]);
        } // just use ints
        int[] pos = nbt.getIntArray(id);
        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    /**
     * Saves a compact representation of {@link BlockPos} to NBT
     *
     * @see #saveCompactBlockPos
     *
     * @param nbt The {@link CompoundTag} to save the compact {@link BlockPos} list to
     * @param id The identifier to use when saving the compact {@link BlockPos} list to the {@link CompoundTag}
     * @param blockPosFunction The function to retrieve the {@link BlockPos} to save for each index
     * @param amt The amount of {@link BlockPos} to save
     * @param max The maximum value that any {@link BlockPos} in the list can have
     */
    public static void saveCompactBlockPosList(CompoundTag nbt, String id, IntFunction<BlockPos> blockPosFunction,
                                               int amt, int max) {
        if (max <= 126) { // block positions fit within 3 bytes
            byte[] positions = new byte[amt * 3];
            int bytePos = 0;
            for (int i = 0; i < amt; i++) {
                BlockPos pos = blockPosFunction.apply(i);
                positions[bytePos++] = (byte)pos.getX();
                positions[bytePos++] = (byte)pos.getY();
                positions[bytePos++] = (byte)pos.getZ();
            }
            nbt.putByteArray(id, positions);
        } else { // just use ints
            int[] positions = new int[amt * 3];
            int intPos = 0;
            for (int i = 0; i < amt; i++) {
                BlockPos pos = blockPosFunction.apply(i);
                positions[intPos++] = pos.getX();
                positions[intPos++] = pos.getY();
                positions[intPos++] = pos.getZ();
            }
            nbt.putIntArray(id, positions);
        }
    }

    /**
     * Loads a list of compact {@link BlockPos} from NBT
     *
     * @param nbt The {@link CompoundTag} to load the compact {@link BlockPos} list from
     * @param id The identifier for the block position list in the {@link CompoundTag}
     * @param max The maximum value that any {@link BlockPos} in the list can have
     * @return A List of {@link BlockPos}
     */
    public static List<BlockPos> loadCompactBlockPosList(CompoundTag nbt, String id, int max) {
        List<BlockPos> blockPosList = new ArrayList<>();
        if (max <= 126) { // relative block positions fit within bytes
            byte[] positions = nbt.getByteArray(id);
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        positions[bytePos++],
                        positions[bytePos++],
                        positions[bytePos++]
                ));
            }
        } else { // just use ints
            int[] positions = nbt.getIntArray(id);
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        positions[bytePos++],
                        positions[bytePos++],
                        positions[bytePos++]
                ));
            }
        }
        return blockPosList;
    }

    /**
     * Saves a list of compact {@link BlockPos} relative to a base position to NBT
     *
     * @see #saveCompactBlockPos
     *
     * @param nbt The {@link CompoundTag} to save the compact relative {@link BlockPos} list to
     * @param id The identifier to use when saving the compact relative {@link BlockPos} list to the {@link CompoundTag}
     * @param basePos The base position to use when calculating the relative positions
     * @param blockPosFunction The function to retrieve the relative {@link BlockPos} to save for each index
     * @param amt The amount of relative {@link BlockPos} to save
     * @param max The maximum value that any relative {@link BlockPos} in the list can have
     */
    public static void saveCompactRelativeBlockPosList(CompoundTag nbt, String id, BlockPos basePos,
                                                       IntFunction<BlockPos> blockPosFunction, int amt, int max) {
        if (max <= 126) { // block positions fit within 3 bytes
            byte[] positions = new byte[amt * 3];
            int bytePos = 0;
            for (int i = 0; i < amt; i++) {
                BlockPos pos = blockPosFunction.apply(i);
                positions[bytePos++] = (byte)(pos.getX() - basePos.getX());
                positions[bytePos++] = (byte)(pos.getY() - basePos.getY());
                positions[bytePos++] = (byte)(pos.getZ() - basePos.getZ());
            }
            nbt.putByteArray(id, positions);
        } else { // just use ints
            int[] positions = new int[amt * 3];
            int intPos = 0;
            for (int i = 0; i < amt; i++) {
                BlockPos pos = blockPosFunction.apply(i);
                positions[intPos++] = pos.getX() - basePos.getX();
                positions[intPos++] = pos.getY() - basePos.getY();
                positions[intPos++] = pos.getZ() - basePos.getZ();
            }
            nbt.putIntArray(id, positions);
        }
    }

    /**
     * Loads a list of compact {@link BlockPos} relative to a base position from NBT
     *
     * @param nbt The {@link CompoundTag} to load the compact relative {@link BlockPos} list from
     * @param id The identifier for the relative block position list in the {@link CompoundTag}
     * @param basePos The base position to use when calculating the relative positions
     * @param max The maximum value that any relative {@link BlockPos} in the list can have
     * @return A List of {@link BlockPos}
     */
    public static List<BlockPos> loadCompactRelativeBlockPosList(CompoundTag nbt, String id,
                                                                 BlockPos basePos, int max) {
        List<BlockPos> blockPosList = new ArrayList<>();
        if (max <= 126) { // relative block positions fit within bytes
            byte[] positions = nbt.getByteArray(id);
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        basePos.getX() + positions[bytePos++],
                        basePos.getY() + positions[bytePos++],
                        basePos.getZ() + positions[bytePos++]
                ));
            }
        } else { // just use ints
            int[] positions = nbt.getIntArray(id);
            int bytePos = 0;
            for (int i = 0; i < positions.length / 3; i++) {
                blockPosList.add(new BlockPos(
                        basePos.getX() + positions[bytePos++],
                        basePos.getY() + positions[bytePos++],
                        basePos.getZ() + positions[bytePos++]
                ));
            }
        }
        return blockPosList;
    }
}
