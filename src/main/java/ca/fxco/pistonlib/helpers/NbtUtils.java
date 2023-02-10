package ca.fxco.pistonlib.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class NbtUtils {

    public static void loadAllItems(CompoundTag compoundTag, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (compoundTag.contains("" + i, Tag.TAG_COMPOUND)) {
                container.setItem(i, ItemStack.of(compoundTag.getCompound("" + i)));
            } else {
                container.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    public static void saveAllItems(CompoundTag compoundTag, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                compoundTag.put("" + i, container.getItem(i).save(new CompoundTag()));
            }
        }
    }
}
