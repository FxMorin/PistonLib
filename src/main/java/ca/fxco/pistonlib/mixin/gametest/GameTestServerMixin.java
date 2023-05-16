package ca.fxco.pistonlib.mixin.gametest;

import net.minecraft.gametest.framework.GameTestServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(GameTestServer.class)
public abstract class GameTestServerMixin {

    // TODO: Keep the server alive, so you can join it. Not sure how to do the joining part

    /*@Unique
    private boolean keepAlive = false;

    @Inject(
            method = "tickServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;tickServer(Ljava/util/function/BooleanSupplier;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void tickNormally(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (keepAlive) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "tickServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gametest/framework/GameTestServer;halt(Z)V"
            )
    )
    private void preventHalt(GameTestServer instance, boolean b) {
        if (!Boolean.parseBoolean(System.getProperty("fabric-api.gametest.keepAlive", "false"))) {
            instance.halt(b);
        } else {
            keepAlive = true;
        }
    }*/
}
