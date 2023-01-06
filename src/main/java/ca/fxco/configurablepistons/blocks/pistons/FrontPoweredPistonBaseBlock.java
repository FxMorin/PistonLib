package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class FrontPoweredPistonBaseBlock extends BasicPistonBaseBlock {

	public FrontPoweredPistonBaseBlock(boolean sticky) {
        super(sticky);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        // Implementation that allows power received through the piston face.
        return level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
    }
}
