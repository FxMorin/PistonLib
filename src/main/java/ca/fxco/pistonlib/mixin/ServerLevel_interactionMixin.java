package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.PistonEventData;
import ca.fxco.pistonlib.network.PLNetwork;
import ca.fxco.pistonlib.network.packets.ClientboundPistonEventPacket;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import ca.fxco.api.pistonlib.pistonLogic.structure.StructureRunner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevel_interactionMixin extends Level {

    private ServerLevel_interactionMixin(WritableLevelData data, ResourceKey<Level> key, Holder<DimensionType> dimension,
                                         Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long seed,
                                         int maxChainedNeighborUpdates) {
        super(data, key, dimension, profiler, isClientSide, isDebug, seed, maxChainedNeighborUpdates);
    }

    @Unique
    private final Set<PistonEventData> pl$pistonEvents = new HashSet<>();

    @Override
    public void pl$addPistonEvent(BasicPistonBaseBlock pistonBase, BlockPos pos, Direction dir, boolean extend) {
        this.pl$pistonEvents.add(new PistonEventData(pistonBase, pos, dir, extend));
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;runBlockEvents()V"
            )
    )
    private void afterBlockEvents(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        pl$runPistonEvents();
    }

    private void pl$runPistonEvents() {
        Set<PistonEventData> runningPistonEvents = new HashSet<>(this.pl$pistonEvents);
        this.pl$pistonEvents.clear();
        for (PistonEventData pistonEvent : runningPistonEvents) {
            BasicPistonBaseBlock pistonBase = pistonEvent.pistonBlock();
            StructureRunner structureRunner = new DecoupledStructureRunner(pistonBase.newStructureRunner(
                    this,
                    pistonEvent.pos(),
                    pistonEvent.dir(),
                    1, // Can't use length in decoupled piston logic
                    pistonEvent.extend(),
                    pistonBase::newStructureResolver
            ));
            if (structureRunner.run()) {
                PLNetwork.sendToClientsInRange(
                        this.getServer(),
                        GlobalPos.of(this.dimension(), pistonEvent.pos()),
                        new ClientboundPistonEventPacket(pistonEvent),
                        64
                );
            }
        }
    }
}
