package ca.fxco.pistonlib.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetworkUtils {

    public static void broadcast(List<ServerPlayer> serverPlayers, @Nullable Player player,
                                 double d, double e, double f, double g,
                                 ResourceKey<Level> dimensionKey, ResourceLocation packetId, FriendlyByteBuf byteBuf) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            if (serverPlayer != player && serverPlayer.level.dimension() == dimensionKey) {
                double h = d - serverPlayer.getX();
                double j = e - serverPlayer.getY();
                double k = f - serverPlayer.getZ();
                if (h * h + j * j + k * k < g * g) {
                    ServerPlayNetworking.send(serverPlayer, packetId, byteBuf);
                }
            }
        }
    }
}
