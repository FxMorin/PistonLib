package ca.fxco.pistonlib.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ILevel {

    void prepareBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity);

    BlockEntity getBlockEntityForPlacement(BlockPos pos, BlockState state);

}
