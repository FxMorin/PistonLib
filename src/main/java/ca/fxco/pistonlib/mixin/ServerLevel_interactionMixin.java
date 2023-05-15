package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.level.ServerLevelInteraction;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.PistonEventData;
import ca.fxco.pistonlib.network.PLNetwork;
import ca.fxco.pistonlib.network.packets.ClientboundPistonEventPacket;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import ca.fxco.pistonlib.pistonLogic.structureRunners.StructureRunner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevel_interactionMixin extends Level implements ServerLevelInteraction {

    protected ServerLevel_interactionMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey,
                                           Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier,
                                           boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
    }

    @Shadow public abstract @NotNull MinecraftServer getServer();

    @Unique
    private final Set<PistonEventData> pistonEvents = new HashSet<>();

    @Override
    public void triggerPistonEvent(BasicPistonBaseBlock pistonBlock, BlockPos pos, Direction dir, boolean extend) {
        this.pistonEvents.add(new PistonEventData(pistonBlock, pos, dir, extend));
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;runBlockEvents()V"
            )
    )
    private void afterBlockEvents(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        runPistonEvents();
    }

    private void runPistonEvents() {
        Set<PistonEventData> runningPistonEvents = new HashSet<>(pistonEvents);
        pistonEvents.clear();
        for (PistonEventData pistonEventData : runningPistonEvents) {
            BasicPistonBaseBlock pistonBlock = pistonEventData.pistonBlock();
            StructureRunner structureRunner = new DecoupledStructureRunner(pistonBlock.newStructureRunner(
                    this,
                    pistonEventData.pos(),
                    pistonEventData.dir(),
                    1, // Can't use length in decoupled piston logic
                    pistonEventData.extend(),
                    pistonBlock::newStructureResolver
            ));
            if (structureRunner.run()) {
                PLNetwork.sendToClientsInRange(
                        this.getServer(),
                        GlobalPos.of(this.dimension(), pistonEventData.pos()),
                        new ClientboundPistonEventPacket(pistonEventData),
                        64
                );
            }
        }
    }
}
