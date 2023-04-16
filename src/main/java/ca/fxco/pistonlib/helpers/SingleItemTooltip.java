package ca.fxco.pistonlib.helpers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;

public class SingleItemTooltip extends BundleTooltip {
    public SingleItemTooltip(NonNullList<ItemStack> nonNullList, int i) {
        super(nonNullList, i);
    }
}
