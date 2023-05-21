package ca.fxco.pistonlib.mixin.gametest;

import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.gametest.expansion.GameTestPlayerList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.BooleanSupplier;

@Mixin(GameTestServer.class)
public abstract class GameTestServerMixin extends MinecraftServer {

    private static final Path DEV_RESOURCES = Path.of("..","..", "src", "main", "resources", "data", "pistonlib", "gametest", "structures");

    @Unique
    private boolean keepAlive = false;
    @Unique
    private boolean tickWarp = false; // TODO: Add command to change gametest config options, true by default

    public GameTestServerMixin(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void useDevResourcesAsTestStructureDir(Thread thread,
                                                   LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                                   PackRepository packRepository, WorldStem worldStem,
                                                   Collection collection, BlockPos blockPos, CallbackInfo ci) {
        StructureUtils.testStructuresDir = DEV_RESOURCES.toString();
        System.out.println("New test structures dir: " + StructureUtils.testStructuresDir);
    }

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
        if (!PistonLib.KEEPALIVE_ACTIVE) {
            instance.halt(b);
        } else {
            keepAlive = true;
        }
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/Services"
            )
    )
    private static Services useNormalServices(MinecraftSessionService minecraftSessionService,
                                              SignatureValidator signatureValidator,
                                              GameProfileRepository gameProfileRepository,
                                              GameProfileCache gameProfileCache) {
        return PistonLib.KEEPALIVE_ACTIVE ?
                Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), new File(".")) :
                new Services(null, SignatureValidator.NO_VALIDATION, null, null);
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;<init>(Ljava/lang/Thread;" +
                            "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;" +
                            "Lnet/minecraft/server/packs/repository/PackRepository;" +
                            "Lnet/minecraft/server/WorldStem;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;" +
                            "Lnet/minecraft/server/Services;" +
                            "Lnet/minecraft/server/level/progress/ChunkProgressListenerFactory;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void setServerInfo(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess,
                               PackRepository packRepository, WorldStem worldStem, Collection collection,
                               BlockPos blockPos, CallbackInfo ci) {
        if (PistonLib.KEEPALIVE_ACTIVE) {
            customPublishServer(GameType.CREATIVE, 25565);
        }
    }

    public void customPublishServer(GameType gameType, int port) {
        try {
            this.getConnection().startTcpServerListener(null, port);
        } catch (IOException var7) {
            System.out.println("**** FAILED TO BIND TO PORT!");
            System.out.println("Perhaps a server is already running on that port?");
            var7.printStackTrace();
        }
        System.out.println("Started serving on " + port);
        this.setMotd("PistonLib GameTest Server");
        this.setLocalIp("127.0.0.1");
        this.setPreventProxyConnections(false);
        this.setPort(port);
        this.setDemo(false);
        this.setDefaultGameType(gameType);
        this.setUsesAuthentication(false);
        this.setPvpAllowed(false);
        this.setFlightAllowed(true);
        this.setEnforceWhitelist(false);
        this.initializeKeyPair();

        this.setPlayerList(new GameTestPlayerList((GameTestServer)(Object)this, this.registries(), this.playerDataStorage));
        SkullBlockEntity.setup(this.services, this);
        GameProfileCache.setUsesAuthentication(false);
    }

    /**
     * @author FX
     * @reason Should be set to dedicated due to it being accessible by players
     */
    @Overwrite
    public boolean isDedicatedServer() {
        return PistonLib.KEEPALIVE_ACTIVE;
    }

    /**
     * @author FX
     * @reason Allow players to see the server
     */
    @Overwrite
    public boolean isPublished() {
        return PistonLib.KEEPALIVE_ACTIVE;
    }

    @Override
    public int getProfilePermissions(GameProfile gameProfile) {
        return 4;
    }

    /**
     * @author FX
     * @reason Change the gametest server tick speed
     */
    @Overwrite
    public void waitUntilNextTick() {
        if (this.tickWarp) {
            this.runAllTasks();
        } else {
            super.waitUntilNextTick();
        }
    }
}
