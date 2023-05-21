package ca.fxco.pistonlib.gametest.expansion;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class GameTestPlayerList extends PlayerList {

    public GameTestPlayerList(GameTestServer gameTestServer, LayeredRegistryAccess<RegistryLayer> registryAccess,
                              PlayerDataStorage playerDataStorage) {
        super(gameTestServer, registryAccess, playerDataStorage, 1);
        this.setViewDistance(10);
        this.setSimulationDistance(10);
    }

    public boolean isWhiteListed(GameProfile gameProfile) {
        return false;
    }

    public GameTestServer getServer() {
        return (GameTestServer)super.getServer();
    }

    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        return true;
    }
}
