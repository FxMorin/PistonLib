package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.properties.PistonType;

public class PushLimitPistonBaseBlock extends BasicPistonBaseBlock {

    protected final int pushLimit;

    public PushLimitPistonBaseBlock(PistonType type, int pushLimit) {
        super(type);

        this.pushLimit = pushLimit;
    }

    @Override
    public PistonStructureResolver newStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurablePistonStructureResolver(this, level, pos, facing, extend, this.pushLimit);
    }
}
