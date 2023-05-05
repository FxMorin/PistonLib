import ca.fxco.pistonlib.helpers.NbtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockPosCompressionTest {

    public static void main(String[] args) {
        test3Byte();
        //test3Short();
        test3Int();
    }

    private static void test3Byte() {
        System.out.println("Testing 3 byte compression");
        // Test lower bounds
        List<BlockPos> originalList = new ArrayList<>();
        for (int x = -126; x < 126; x++) {
            for (int y = -126; y < 126; y++) {
                for (int z = -126; z < 126; z++) {
                    originalList.add(new BlockPos(x, y, z));
                }
            }
        }
        System.out.println("Size: " + originalList.size());
        CompoundTag testTag = new CompoundTag();
        NbtUtils.saveCompactBlockPosList(testTag, "test", originalList::get, originalList.size(), 126);
        System.out.println("Size In Bytes: " + testTag.sizeInBytes());
        List<BlockPos> newList = NbtUtils.loadCompactBlockPosList(testTag, "test", 126);
        int fails = 0;
        for (int i = 0; i < originalList.size(); i++) {
            BlockPos orgPos = originalList.get(i);
            BlockPos newPos = newList.get(i);
            if (!orgPos.equals(newPos)) {
                fails++;
                System.out.println("original: [" + orgPos.toShortString() + "] - new: [" + newPos.toShortString() + "]");
            }
        }
        System.out.println("3 byte compression: " + (fails > 0 ? "FAILED" : "SUCCESS"));
    }

    /*private static void test3Short() {
        System.out.println("Testing 3 short compression");
        Random random = new Random();
        // Test lower bounds
        List<BlockPos> originalList = new ArrayList<>();
        for (int c = 0; c < 16003008; c++) { // The same amount as test3Byte
            originalList.add(new BlockPos(
                    random.nextInt(-32766, 32766),
                    random.nextInt(-32766, 32766),
                    random.nextInt(-32766, 32766)
            ));
        }
        System.out.println("Size: " + originalList.size());
        CompoundTag testTag = new CompoundTag();
        NbtUtils.saveCompactBlockPosList(testTag, "test", originalList::get, originalList.size(), 32766);
        System.out.println("Size In Bytes: " + testTag.sizeInBytes());
        List<BlockPos> newList = NbtUtils.loadCompactBlockPosList(testTag, "test", 32766);
        int fails = 0;
        for (int i = 0; i < originalList.size(); i++) {
            BlockPos orgPos = originalList.get(i);
            BlockPos newPos = newList.get(i);
            if (!orgPos.equals(newPos)) {
                fails++;
                System.out.println("original: [" + orgPos.toShortString() + "] - new: [" + newPos.toShortString() + "]");
            }
        }
        System.out.println("3 short compression: " + (fails > 0 ? "FAILED" : "SUCCESS"));
    }*/

    private static void test3Int() {
        System.out.println("Testing 3 int compression");
        Random random = new Random();
        // Test lower bounds
        List<BlockPos> originalList = new ArrayList<>();
        for (int c = 0; c < 16003008; c++) { // The same amount as test3Byte
            originalList.add(new BlockPos(
                    random.nextInt(),
                    random.nextInt(),
                    random.nextInt()
            ));
        }
        System.out.println("Size: " + originalList.size());
        CompoundTag testTag = new CompoundTag();
        NbtUtils.saveCompactBlockPosList(testTag, "test", originalList::get, originalList.size(), Integer.MAX_VALUE);
        System.out.println("Size In Bytes: " + testTag.sizeInBytes());
        List<BlockPos> newList = NbtUtils.loadCompactBlockPosList(testTag, "test", Integer.MAX_VALUE);
        int fails = 0;
        for (int i = 0; i < originalList.size(); i++) {
            BlockPos orgPos = originalList.get(i);
            BlockPos newPos = newList.get(i);
            if (!orgPos.equals(newPos)) {
                fails++;
                System.out.println("original: [" + orgPos.toShortString() + "] - new: [" + newPos.toShortString() + "]");
            }
        }
        System.out.println("3 int compression: " + (fails > 0 ? "FAILED" : "SUCCESS"));
    }
}
