package ca.fxco.pistonlib.blocks;

import ca.fxco.api.pistonlib.block.ConfigurablePistonBehavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PullOnlyBlock extends Block implements ConfigurablePistonBehavior {

	public PullOnlyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean canPistonPush(Level level, BlockPos pos, BlockState state, Direction dir) {
        return false;
    }
}
