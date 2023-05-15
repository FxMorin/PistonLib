package ca.fxco.pistonlib.network.packets;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.PistonEventData;
import ca.fxco.pistonlib.pistonLogic.structureRunners.DecoupledStructureRunner;
import ca.fxco.pistonlib.pistonLogic.structureRunners.StructureRunner;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

@AllArgsConstructor
@NoArgsConstructor
public class ClientboundPistonEventPacket extends PLPacket {

    private BasicPistonBaseBlock pistonBlock;
    private BlockPos pos;
    private Direction dir;
    private boolean extend;

    public ClientboundPistonEventPacket(PistonEventData pistonEventData) {
        this(pistonEventData.pistonBlock(), pistonEventData.pos(), pistonEventData.dir(), pistonEventData.extend());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.BLOCK, this.pistonBlock);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.dir.ordinal());
        buf.writeBoolean(this.extend);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.pistonBlock = (BasicPistonBaseBlock) buf.readById(BuiltInRegistries.BLOCK);
        this.pos = buf.readBlockPos();
        this.dir = Direction.values()[buf.readByte()];
        this.extend = buf.readBoolean();
    }

    @Override
    public void handleClient(Minecraft client, PacketSender packetSender) {
        StructureRunner structureRunner = new DecoupledStructureRunner(this.pistonBlock.newStructureRunner(client.level, this.pos, this.dir, 1, this.extend, this.pistonBlock::newStructureResolver));
        structureRunner.run();
    }
}
