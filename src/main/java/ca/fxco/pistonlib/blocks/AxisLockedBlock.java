package ca.fxco.pistonlib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AxisLockedBlock extends RotatedPillarBlock {

    public AxisLockedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean pl$usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean pl$canPistonPush(Level level, BlockPos pos, BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS);
    }

    @Override
    public boolean pl$canPistonPull(Level level, BlockPos pos, BlockState state, Direction dir) {
        return dir.getAxis() == state.getValue(AXIS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(AXIS, ctx.getNearestLookingDirection().getAxis());
    }
}
