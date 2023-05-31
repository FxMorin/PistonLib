package ca.fxco.api.pistonlib.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelMBE {

    void pl$prepareBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity);

    BlockEntity pl$getBlockEntityForPlacement(BlockPos pos, BlockState state);

}
