package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.helpers.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class StalePistonBaseBlock extends BasicPistonBaseBlock {

    public StalePistonBaseBlock(PistonType type) {
        super(type);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing);
    }
}