package ca.fxco.api.pistonlib.level;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface LevelPistonInteraction {

    void pl$addPistonEvent(BasicPistonBaseBlock pistonBase, BlockPos pos, Direction dir, boolean extend);

}
