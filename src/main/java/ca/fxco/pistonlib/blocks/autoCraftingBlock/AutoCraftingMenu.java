package ca.fxco.pistonlib.blocks.autoCraftingBlock;

import ca.fxco.pistonlib.base.ModMenus;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class AutoCraftingMenu extends AbstractContainerMenu {

    //private final CraftingContainer craftSlots;
    //private final ResultContainer resultSlots;
    //private final ContainerLevelAccess access;
    private final Container container;
    private final Player player;

    public AutoCraftingMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(10));
    }

    public AutoCraftingMenu(int containerId, Inventory inventory, Container container) {
        super(ModMenus.AUTO_CRAFTING, containerId);
        //this.craftSlots = new CraftingContainer(this, 3, 3);
        //this.resultSlots = new ResultContainer();
        this.container = container;
        //this.access = ContainerLevelAccess.NULL;
        this.player = inventory.player;
        this.addSlot(new ReadOnlySlot(this.container, 9, 124, 35, false));

        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 3; ++k) {
                this.addSlot(new ReadOnlySlot(this.container, k + j * 3, 30 + k * 18, 17 + j * 18, true));
            }
        }
    }

    @Override
    public void clicked(int i, int j, ClickType clickType, Player player) {
        // Remove the ability to interact, this is a display only menu
    }

    @Override
    protected void clearContainer(Player player, Container container) {}

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return false; //slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // Not sure about this yet
    }

    @Override
    public void slotsChanged(Container container) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, 0, 0, this.container.getItem(0)));
        }
    }

    @Override
    public void removed(Player player) {
        //ItemStack cursorStack = this.player.containerMenu.getCarried();
        //if (!cursorStack.isEmpty()) {
        //    player.drop(cursorStack, false);
        //    this.player.containerMenu.setCarried(ItemStack.EMPTY);
        //}
        //this.blockEntity.onMenuClose(this);
    }
}
