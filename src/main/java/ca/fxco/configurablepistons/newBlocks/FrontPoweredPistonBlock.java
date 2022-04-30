package ca.fxco.configurablepistons.newBlocks;

import ca.fxco.configurablepistons.base.BasicPistonBlock;
import ca.fxco.configurablepistons.base.BasicPistonExtensionBlock;
import ca.fxco.configurablepistons.base.BasicPistonHeadBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FrontPoweredPistonBlock extends BasicPistonBlock {
    public FrontPoweredPistonBlock(boolean sticky) {
        super(sticky);
    }
    public FrontPoweredPistonBlock(boolean sticky, Settings settings) {
        super(sticky, settings);
    }
    public FrontPoweredPistonBlock(boolean sticky, BasicPistonExtensionBlock extensionBlock, BasicPistonHeadBlock headBlock) {
        super(sticky, extensionBlock, headBlock);
    }

    @Override
    public boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
        for(Direction dir : Direction.values()) {
            if (world.isEmittingRedstonePower(pos.offset(dir), dir)) return true;
        }
        if (world.isEmittingRedstonePower(pos, Direction.DOWN)) return true;
        BlockPos blockPos = pos.up();
        for(Direction dir : Direction.values()) {
            if (dir != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(dir), dir)) return true;
        }
        return false;
    }
}
