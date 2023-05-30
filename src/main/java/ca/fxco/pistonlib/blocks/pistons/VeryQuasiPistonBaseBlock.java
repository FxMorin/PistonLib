package ca.fxco.pistonlib.blocks.pistons;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.Utils;
import ca.fxco.api.pistonlib.level.QLevel;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class VeryQuasiPistonBaseBlock extends BasicPistonBaseBlock {

    private final int quasiStrength; // Use 1 to replicate vanilla behaviour

	public VeryQuasiPistonBaseBlock(PistonFamily family, PistonType type, int quasiStrength) {
        super(family, type);

        this.quasiStrength = quasiStrength;
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) ||
                ((QLevel)level).hasQuasiNeighborSignalColumn(pos, this.quasiStrength);
    }
}
