package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.pistonLogic.pistonHandlers.ConfigurablePistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PushLimitPistonBlock extends BasicPistonBlock {

    protected final int pushLimit;

    public PushLimitPistonBlock(boolean sticky, int pushLimit) {
        super(sticky);
        this.pushLimit = pushLimit;
    }

    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurablePistonHandler(world, pos, dir, retract, this.pushLimit);
    }
}
