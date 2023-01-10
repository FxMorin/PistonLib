package ca.fxco.pistonlib.blocks.pistons.movableBlockEntities;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicMovingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class MBEMovingBlock extends BasicMovingBlock {

    @Override
    public BlockEntity createMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState,
                                               @Nullable BlockEntity movedBlockEntity, Direction facing,
                                               boolean extending, boolean isSourcePiston) {
        return new MBEMovingBlockEntity(pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModBlockEntities.MBE_MOVING_BLOCK_ENTITY);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Level level = builder.getLevel();
        Vec3 origin = builder.getParameter(LootContextParams.ORIGIN);
        MBEMovingBlockEntity mbe = this.getMovingBlockEntity(level, new BlockPos(origin));

        if (mbe == null) {
            return Collections.emptyList();
        }

        BlockState movedState = mbe.getMovedState();
        BlockEntity movedBlockEntity = mbe.getMovedBlockEntity();

        if (mbe == builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)) {
            builder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, movedBlockEntity);
        }

        return movedState.getDrops(builder);
    }

    @Nullable
    private MBEMovingBlockEntity getMovingBlockEntity(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MBEMovingBlockEntity mbe ? mbe : null;
    }
}
