package ca.fxco.pistonlib.blocks.pistons;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.structureResolvers.BasicStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class PushLimitPistonBaseBlock extends BasicPistonBaseBlock {

    protected final int pushLimit;

    public PushLimitPistonBaseBlock(PistonFamily family, PistonType type, int pushLimit) {
        super(family, type);

        this.pushLimit = pushLimit;
    }

    @Override
    public BasicStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new BasicStructureResolver(this, level, pos, facing, extend, this.pushLimit);
    }
}
