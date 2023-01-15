package ca.fxco.pistonlib.blocks.pistons;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.pistonlib.impl.QLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class VeryQuasiPistonBaseBlock extends BasicPistonBaseBlock {

    private final int quasiStrength; // Use 1 to replicate vanilla behaviour

	public VeryQuasiPistonBaseBlock(int quasiStrength, PistonType type) {
        super(type);
        this.quasiStrength = quasiStrength;
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) ||
                ((QLevel)level).hasQuasiNeighborSignalColumn(pos, this.quasiStrength);
    }
}
