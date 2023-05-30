package ca.fxco.pistonlib.blocks.pistons.veryStickyPiston;

import java.util.Map;

import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.api.pistonlib.block.ConfigurablePistonBehavior;
import ca.fxco.api.pistonlib.block.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class VeryStickyPistonBaseBlock extends BasicPistonBaseBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public VeryStickyPistonBaseBlock(PistonFamily family) {
        super(family, PistonType.STICKY);
    }

    // I want to create a diagonal block entity instead of just teleporting blocks
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved || !state.getValue(EXTENDED)) return;
        BlockPos offsetPos = pos.relative(state.getValue(FACING));
        BlockPos offsetPos2 = offsetPos.relative(state.getValue(FACING));
        BlockState blockState = world.getBlockState(offsetPos2);
        if (blockState.getBlock() == this.getFamily().getHead()) {
            world.setBlock(offsetPos, blockState, UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE | UPDATE_MOVE_BY_PISTON);
            world.setBlock(offsetPos2, Blocks.AIR.defaultBlockState(),
                UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE | UPDATE_MOVE_BY_PISTON);
        }
    }

    // Automatically makes it movable even when extended
    @Override
    public boolean usesConfigurablePistonBehavior() {
        return true;
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return true;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.getValue(FACING),StickyType.STICKY); // Sticky Front
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        return dir == state.getValue(FACING) ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
