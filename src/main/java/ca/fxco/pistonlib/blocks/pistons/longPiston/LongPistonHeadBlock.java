package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;

import net.minecraft.world.level.block.state.BlockState;

public class LongPistonHeadBlock extends BasicPistonHeadBlock {

    public LongPistonHeadBlock(PistonFamily family) {
        super(family);
    }

    public LongPistonHeadBlock(PistonFamily family, Properties settings) {
        super(family, settings);
    }

    @Override
    public boolean isFittingBase(BlockState headState, BlockState behindState) {
        return behindState.is(this.family.getArm()) ?
                behindState.getValue(FACING) == headState.getValue(FACING) :
                super.isFittingBase(headState, behindState);
    }
}
