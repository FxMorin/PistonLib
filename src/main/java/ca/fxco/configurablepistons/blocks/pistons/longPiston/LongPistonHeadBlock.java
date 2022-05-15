package ca.fxco.configurablepistons.blocks.pistons.longPiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonHeadBlock;
import net.minecraft.block.*;

public class LongPistonHeadBlock extends BasicPistonHeadBlock {

    public LongPistonHeadBlock() {
        super();
    }

    public LongPistonHeadBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isAttached(BlockState headState, BlockState backState) {
        return backState.getBlock() instanceof LongPistonArmBlock ?
                headState.get(TYPE) == backState.get(TYPE) && backState.get(FACING) == headState.get(FACING) :
                super.isAttached(headState, backState);
    }
}
