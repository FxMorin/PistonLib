package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServer_savingMixin {

    @Inject(
            method = "stopServer",
            at = @At("HEAD")
    )
    private void onStopServer(CallbackInfo ci) {
        PistonLib.onStopServer();
    }

    @Inject(
            method = "saveEverything",
            at = @At("HEAD")
    )
    private void savePistonMoveBehaviorOverrides(boolean quietly, boolean bl1, boolean bl2,
                                                 CallbackInfoReturnable<Boolean> cir) {
        PistonLibBehaviorManager.save(quietly);
    }
}
