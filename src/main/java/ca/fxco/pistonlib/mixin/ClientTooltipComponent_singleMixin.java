package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.helpers.SingleClientBundleTooltip;
import ca.fxco.pistonlib.helpers.SingleItemTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponent_singleMixin {

    @Inject(
            method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)" +
                    "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onCreate(TooltipComponent tooltipComponent, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (tooltipComponent instanceof SingleItemTooltip sit) {
            cir.setReturnValue(new SingleClientBundleTooltip(sit));
        }
    }
}
