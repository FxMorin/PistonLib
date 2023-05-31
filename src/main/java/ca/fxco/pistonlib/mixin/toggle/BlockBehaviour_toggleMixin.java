package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(BlockBehaviour.class)
public class BlockBehaviour_toggleMixin implements PLBlockBehaviour {

    @Unique
    private BooleanSupplier isDisabled;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;" +
                            "requiredFeatures:Lnet/minecraft/world/flag/FeatureFlagSet;",
                    shift = At.Shift.AFTER
            )
    )
    private void onInit(BlockBehaviour.Properties properties, CallbackInfo ci) {
        this.isDisabled = properties.pl$getIsDisabled();
    }

    @Override
    public BooleanSupplier pl$getIsDisabled() {
        return this.isDisabled;
    }

    @Inject(
            method = "requiredFeatures",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableBlock(CallbackInfoReturnable<FeatureFlagSet> cir) {
        if (this.isDisabled.getAsBoolean()) {
            cir.setReturnValue(NEVER_ENABLED_SET);
        }
    }
}
