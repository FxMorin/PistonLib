package ca.fxco.pistonlib.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class NbtUtils {

    public static CompoundTag saveAllItems(CompoundTag compoundTag, Container container) {
        if (container.isEmpty()) {
            return compoundTag;
        }
        ListTag listTag = new ListTag();

        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putByte("Slot", (byte)i);
                itemStack.save(compoundTag2);
                listTag.add(compoundTag2);
            }
        }

        if (!listTag.isEmpty()) {
            compoundTag.put("Items", listTag);
        }

        return compoundTag;
    }

    public static void loadAllItems(CompoundTag compoundTag, Container container) {
        if (compoundTag.contains("Items", 10)) {
            ListTag listTag = compoundTag.getList("Items", 10);

            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                if (j >= 0 && j < container.getContainerSize()) {
                    container.setItem(j, ItemStack.of(compoundTag2));
                }
            }
        }
    }
}
