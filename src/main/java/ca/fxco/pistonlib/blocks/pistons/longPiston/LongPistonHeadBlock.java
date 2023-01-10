package ca.fxco.pistonlib.blocks.pistons.longPiston;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonHeadBlock;

import net.minecraft.world.level.block.state.BlockState;

public class LongPistonHeadBlock extends BasicPistonHeadBlock {

    public LongPistonHeadBlock() {
        super();
    }

    public LongPistonHeadBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFittingBase(BlockState headState, BlockState behindState) {
        return behindState.getBlock() instanceof LongPistonArmBlock ?
                headState.getValue(TYPE) == behindState.getValue(TYPE) && behindState.getValue(FACING) == headState.getValue(FACING) :
                super.isFittingBase(headState, behindState);
    }
}
