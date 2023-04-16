package ca.fxco.pistonlib.helpers;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.inventory.tooltip.BundleTooltip;

public class SingleClientBundleTooltip extends ClientBundleTooltip {
    public SingleClientBundleTooltip(BundleTooltip bundleTooltip) {
        super(bundleTooltip);
    }

    @Override
    public int gridSizeX() {
        return 1;
    }

    @Override
    public int gridSizeY() {
        return 1;
    }
}
