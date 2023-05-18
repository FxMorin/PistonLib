package ca.fxco.pistonlib.network.packets;

import ca.fxco.pistonlib.blocks.gametest.CheckStateBlockEntity;
import ca.fxco.pistonlib.helpers.BlockStateExp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

@AllArgsConstructor
@NoArgsConstructor
public class ServerboundSetCheckStatePacket extends PLPacket {

    private BlockPos blockPos;
    private int tick;
    private boolean failOnFound;
    private boolean invertState;
    private boolean ignorePistons;
    private Direction direction;
    private BlockStateExp blockStateExp;

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeVarInt(tick);
        buf.writeBoolean(failOnFound);
        buf.writeBoolean(invertState);
        buf.writeBoolean(ignorePistons);
        buf.writeByte(direction.ordinal());
        buf.writeBoolean(blockStateExp != null);
        if (blockStateExp != null) {
            buf.writeNbt(blockStateExp.write());
        }
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.tick = buf.readVarInt();
        this.failOnFound = buf.readBoolean();
        this.invertState = buf.readBoolean();
        this.ignorePistons = buf.readBoolean();
        this.direction = Direction.values()[buf.readByte()];
        if (buf.readBoolean()) {
            this.blockStateExp = BlockStateExp.read(buf.readNbt());
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void handleServer(MinecraftServer server, ServerPlayer fromPlayer, PacketSender packetSender) {
        if (fromPlayer.canUseGameMasterBlocks()) {
            BlockEntity blockEntity = fromPlayer.level.getBlockEntity(this.blockPos);
            if (blockEntity instanceof CheckStateBlockEntity checkStateBlockEntity) {
                checkStateBlockEntity.setTick(this.tick);
                checkStateBlockEntity.setFailOnFound(this.failOnFound);
                checkStateBlockEntity.setDirection(this.direction);
                checkStateBlockEntity.setBlockStateExp(this.blockStateExp);

                // TODO: Add checks to make sure the values are valid?

                checkStateBlockEntity.setChanged();
            }
        }
    }
}
