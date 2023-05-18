package ca.fxco.pistonlib.blocks.gametest;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.helpers.BlockStateExp;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Setter
@Getter
public class CheckStateBlockEntity extends BlockEntity {

    private boolean failOnFound = false;
    private boolean invertState = false;
    private boolean ignorePistons = false;
    private int tick = -1; // -1 = no onTick conditions
    private Direction direction = Direction.UP; // TODO: Switch to use a blockstate direction once it has a texture! Maybe? May be cleaner this way
    private BlockStateExp blockStateExp = BlockStateExp.EMPTY;

    public CheckStateBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.CHECK_STATE_BLOCK_ENTITY, pos, state);
    }

    public CheckStateBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Environment(EnvType.CLIENT)
    public boolean usedBy(Player player) {
        if (!player.canUseGameMasterBlocks()) {
            return false;
        }
        if (player instanceof LocalPlayer localPlayer) {
            localPlayer.minecraft.setScreen(new CheckStateScreen(this));
        }
        return true;
    }

    public Boolean runGameTestChecks(GameTestHelper helper, BlockPos checkPos) {
        BlockState checkState = helper.getBlockState(checkPos);
        if (this.isIgnorePistons() && checkState.is(ModTags.MOVING_PISTONS)) {
            if (this.tick > -1) {
                helper.fail("Block at position " + checkPos.toShortString() + ": " + checkState + " is a moving piston, on tick: " + helper.getTick());
                return false;
            }
            return null;
        }
        if (this.getBlockStateExp().matches(checkState) != this.isInvertState()) {
            if (this.isFailOnFound()) {
                helper.fail("Block at position " + checkPos.toShortString() + " is: " + checkState);
                return false;
            }
            return true;
        } else if (this.tick > -1) {
            helper.fail("Block at position " + checkPos.toShortString() + ": " + checkState + " did not match the required expression, on tick: " + helper.getTick());
            return false;
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putBoolean("fail", failOnFound);
        compoundTag.putBoolean("invertState", invertState);
        compoundTag.putBoolean("ignorePistons", ignorePistons);
        compoundTag.putByte("dir", (byte) direction.ordinal());
        if (tick != -1) {
            compoundTag.putInt("tick", tick);
        }
        if (blockStateExp != null) {
            compoundTag.put("BS", blockStateExp.write());
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.failOnFound = compoundTag.getBoolean("fail");
        this.invertState = compoundTag.getBoolean("invertState");
        this.ignorePistons = compoundTag.getBoolean("ignorePistons");
        this.direction = Direction.values()[compoundTag.getByte("dir")];
        this.tick = compoundTag.contains("tick") ? compoundTag.getInt("tick") : -1;
        if (compoundTag.contains("BS")) {
            this.blockStateExp = BlockStateExp.read(compoundTag.getCompound("BS"));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}
