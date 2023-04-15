package ca.fxco.pistonlib.mixin.blockEntity;

import ca.fxco.pistonlib.impl.BlockEntityPostLoad;
import ca.fxco.pistonlib.impl.LevelAdditions;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Post Load block entities once before they tick.
 * This allows the block entities to run code first while only running this logic once
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevel_blockEntityMixin implements LevelAdditions {

    @Unique
    private final Set<BlockEntityPostLoad> blockEntitiesToPostLoad = new HashSet<>();

    @Override
    public void addBlockEntityPostLoad(BlockEntityPostLoad blockEntity) {
        blockEntitiesToPostLoad.add(blockEntity);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;tickTime()V"
            )
    )
    private void beforeBlockEntityTicking(CallbackInfo ci) {
        Iterator<BlockEntityPostLoad> iterator = blockEntitiesToPostLoad.iterator();
        while (iterator.hasNext()) {
            BlockEntityPostLoad blockEntityPostLoad = iterator.next();
            blockEntityPostLoad.onPostLoad();
            iterator.remove();
        }
    }
}
