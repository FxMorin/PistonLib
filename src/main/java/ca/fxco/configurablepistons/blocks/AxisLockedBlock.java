package ca.fxco.configurablepistons.blocks;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AxisLockedBlock extends RotatedPillarBlock implements ConfigurablePistonBehavior {

    public AxisLockedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS);
    }

    @Override
    public boolean canPistonPull(BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS);
    }
}
