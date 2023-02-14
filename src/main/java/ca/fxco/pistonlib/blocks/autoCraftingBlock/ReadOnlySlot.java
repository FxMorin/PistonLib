package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * A slot that cannot be modified by players
 */
public class ReadOnlySlot extends Slot {

    private final boolean single;

    public ReadOnlySlot(Container container, int slot, int x, int y) {
        this(container, slot, x, y, false);
    }

    public ReadOnlySlot(Container container, int slot, int x, int y, boolean single) {
        super(container, slot, x, y);
        this.single = single;
    }

    @Override
    public void onQuickCraft(ItemStack itemStack, ItemStack itemStack2) {}

    @Override
    public int getMaxStackSize() {
        return single ? 1 : this.container.getMaxStackSize();
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public Optional<ItemStack> tryRemove(int i, int j, Player player) {
        return Optional.empty();
    }

    @Override
    public ItemStack safeTake(int i, int j, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean allowModification(Player player) {
        return false;
    }
}
