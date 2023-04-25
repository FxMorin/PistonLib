package ca.fxco.pistonlib.network.packets;

import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

// PistonLib Packet
@NoArgsConstructor
public abstract class PLPacket {

    public abstract void write(FriendlyByteBuf friendlyByteBuf);

    public abstract void read(FriendlyByteBuf friendlyByteBuf);


    public final @NotNull FriendlyByteBuf writeAsBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf);
        return buf;
    }

    /**
     * Called on the render thread!
     */
    public void handleClient(Minecraft client, PacketSender packetSender) {};

    /**
     * Called on the server thread!
     */
    public void handleServer(MinecraftServer server, ServerPlayer fromPlayer, PacketSender packetSender) {};

}
