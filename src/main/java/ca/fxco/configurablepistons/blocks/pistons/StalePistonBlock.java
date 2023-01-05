package ca.fxco.configurablepistons.blocks.pistons;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StalePistonBlock extends BasicPistonBlock {

    public StalePistonBlock(boolean sticky) {
        super(sticky);
    }

    @Override
    public boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for(Direction dir : Direction.values())
            if (dir != pistonFace && world.isEmittingRedstonePower(pos.offset(dir), dir))
                return true;
        return world.isEmittingRedstonePower(pos, Direction.DOWN);
    }
}