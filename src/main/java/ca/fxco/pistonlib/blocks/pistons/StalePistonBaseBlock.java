package ca.fxco.pistonlib.blocks.pistons;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class StalePistonBaseBlock extends BasicPistonBaseBlock {

    public StalePistonBaseBlock(boolean isSticky) {
        super(isSticky);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing);
    }
}