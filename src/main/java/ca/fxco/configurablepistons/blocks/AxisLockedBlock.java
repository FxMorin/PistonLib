package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.Direction;

public class AxisLockedBlock extends PillarBlock implements ConfigurablePistonBehavior {

    public AxisLockedBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    public boolean canPistonPush(BlockState state, Direction direction) {
        return state.get(AXIS) == direction.getAxis();
    }

    public boolean canPistonPull(BlockState state, Direction direction) {
        return state.get(AXIS) == direction.getAxis();
    }
}
