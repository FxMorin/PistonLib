package ca.fxco.configurablepistons.newBlocks;

import ca.fxco.configurablepistons.basePistons.BasicPistonBlock;
import ca.fxco.configurablepistons.helpers.ConfigurablePistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PushLimitPistonBlock extends BasicPistonBlock {

    private final int pushLimit;

    public PushLimitPistonBlock(boolean sticky, int pushLimit) {
        super(sticky);
        this.pushLimit = pushLimit;
    }

    public ConfigurablePistonHandler getPistonHandler(World world, BlockPos pos, Direction dir, boolean retract) {
        return new ConfigurablePistonHandler(world, pos, dir, retract, this.pushLimit);
    }
}
