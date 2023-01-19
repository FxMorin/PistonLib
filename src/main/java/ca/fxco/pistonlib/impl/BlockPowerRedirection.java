package ca.fxco.pistonlib.impl;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockPowerRedirection {

    boolean canRedirectRedstone(BlockState blockState, @Nullable Direction direction);

}
