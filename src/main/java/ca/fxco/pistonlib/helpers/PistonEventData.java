package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record PistonEventData(BasicPistonBaseBlock pistonBlock, BlockPos pos, Direction dir, boolean extend) {
    public PistonEventData(BasicPistonBaseBlock pistonBlock, BlockPos pos, Direction dir, boolean extend) {
        this.pistonBlock = pistonBlock;
        this.pos = pos;
        this.dir = dir;
        this.extend = extend;
    }
}
