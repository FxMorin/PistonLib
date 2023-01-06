package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonStructureResolver;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class PushLimitPistonBaseBlock extends BasicPistonBaseBlock {

    protected final int pushLimit;

    public PushLimitPistonBaseBlock(boolean isSticky, int pushLimit) {
        super(isSticky);
        this.pushLimit = pushLimit;
    }

    @Override
    public ConfigurablePistonStructureResolver createStructureResolver(Level level, BlockPos pos, Direction facing, boolean extend) {
        return new ConfigurablePistonStructureResolver(level, pos, facing, extend, this.pushLimit);
    }
}
