package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.pistonlib.impl.toggle.Toggleable;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(Item.class)
public class Item_toggleMixin implements Toggleable {
    
    @Unique
    private BooleanSupplier isDisabled;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/Item$Properties;" +
                            "requiredFeatures:Lnet/minecraft/world/flag/FeatureFlagSet;",
                    shift = At.Shift.AFTER
            )
    )
    private void onInit(Item.Properties properties, CallbackInfo ci) {
        this.isDisabled = ((Toggleable)properties).getIsDisabled();
    }

    @Override
    public BooleanSupplier getIsDisabled() {
        return this.isDisabled;
    }

    @Inject(
            method = "requiredFeatures",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableItem(CallbackInfoReturnable<FeatureFlagSet> cir) {
        if (this.isDisabled.getAsBoolean()) {
            cir.setReturnValue(Toggleable.NEVER_ENABLED_SET);
        }
    }
}
