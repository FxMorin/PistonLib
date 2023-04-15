package ca.fxco.api.pistonlib.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CrushingContainer implements Container {

    private final NonNullList<ItemStack> items;
    private final @Nullable Block againstBlock;

    public CrushingContainer(NonNullList<ItemStack> items) {
        this(items, null);
    }

    public CrushingContainer(NonNullList<ItemStack> items, @Nullable Block againstBlock) {
        this.items = items;//NonNullList.withSize(size, ItemStack.EMPTY);
        this.againstBlock = againstBlock;
    }

    public @Nullable Block getAgainstBlock() {
        return this.againstBlock;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        return i >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(i);
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        return ContainerHelper.removeItem(this.items, slot, amt);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.items.set(slot, itemStack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
