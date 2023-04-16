package ca.fxco.pistonlib.mixin;

import ca.fxco.api.pistonlib.level.ServerLevelInteraction;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.PistonEventData;
import ca.fxco.pistonlib.network.NetworkUtils;
import ca.fxco.pistonlib.network.PistonLibNetworkConstants;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import ca.fxco.pistonlib.pistonLogic.structureRunners.StructureRunner;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
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
            StructureRunner structureRunner = new DecoupledStructureRunner(pistonBlock.newStructureRunner());
            if (structureRunner.run(this, pistonEventData.pos(), pistonEventData.dir(), pistonEventData.extend(), pistonBlock::newStructureResolver)) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
                friendlyByteBuf.writeId(BuiltInRegistries.BLOCK, pistonBlock);
                friendlyByteBuf.writeBlockPos(pistonEventData.pos());
                friendlyByteBuf.writeByte(pistonEventData.dir().ordinal());
                friendlyByteBuf.writeBoolean(pistonEventData.extend());
                NetworkUtils.broadcast(
                        this.getServer().getPlayerList().getPlayers(),
                        null,
                        pistonEventData.pos().getX(),
                        pistonEventData.pos().getY(),
                        pistonEventData.pos().getZ(),
                        64.0,
                        this.dimension(),
                        PistonLibNetworkConstants.PISTON_EVENT_PACKET_ID,
                        friendlyByteBuf
                );
            }
        }
    }
}
