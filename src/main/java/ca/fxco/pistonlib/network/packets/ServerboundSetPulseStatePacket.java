package ca.fxco.pistonlib.network.packets;

import ca.fxco.pistonlib.blocks.gametest.PulseStateBlockEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@AllArgsConstructor
@NoArgsConstructor
public class ServerboundSetPulseStatePacket extends PLPacket {

    private BlockPos blockPos;
    private int delay;
    private int duration;
    private BlockState firstBlockState;
    private BlockState pulseBlockState;
    private BlockState lastBlockState;
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeVarInt(delay);
        buf.writeVarInt(duration);
        byte set = 0;
        if (firstBlockState != null) {
            set |= (1L << 0);
        }
        if (pulseBlockState != null) {
            set |= (1L << 1);
        }
        if (lastBlockState != null) {
            set |= (1L << 2);
        }
        buf.writeByte(set);
        if (firstBlockState != null) {
            buf.writeNbt(NbtUtils.writeBlockState(firstBlockState));
        }
        if (pulseBlockState != null) {
            buf.writeNbt(NbtUtils.writeBlockState(pulseBlockState));
        }
        if (lastBlockState != null) {
            buf.writeNbt(NbtUtils.writeBlockState(lastBlockState));
        }
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        HolderGetter<Block> holderGetter = BuiltInRegistries.BLOCK.asLookup();
        this.blockPos = buf.readBlockPos();
        this.delay = buf.readVarInt();
        this.duration = buf.readVarInt();
        byte set = buf.readByte();
        if ((set & (1L << 0)) != 0) {
            this.firstBlockState = NbtUtils.readBlockState(holderGetter, buf.readNbt());
        }
        if ((set & (1L << 1)) != 0) {
            this.pulseBlockState = NbtUtils.readBlockState(holderGetter, buf.readNbt());
        }
        if ((set & (1L << 2)) != 0) {
            this.lastBlockState = NbtUtils.readBlockState(holderGetter, buf.readNbt());
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void handleServer(MinecraftServer server, ServerPlayer fromPlayer, PacketSender packetSender) {
        if (fromPlayer.canUseGameMasterBlocks()) {
            BlockEntity blockEntity = fromPlayer.level.getBlockEntity(this.blockPos);
            if (blockEntity instanceof PulseStateBlockEntity pulseStateBlockEntity) {
                pulseStateBlockEntity.setDelay(this.delay);
                pulseStateBlockEntity.setDuration(this.duration);
                pulseStateBlockEntity.setFirstBlockState(this.firstBlockState);
                pulseStateBlockEntity.setPulseBlockState(this.pulseBlockState);
                pulseStateBlockEntity.setLastBlockState(this.lastBlockState);

                // TODO: Add checks to make sure the values are valid?

                pulseStateBlockEntity.setChanged();
            }
        }
    }
}
