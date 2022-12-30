package ca.fxco.configurablepistons.blocks.pistons.veryStickyPiston;

import ca.fxco.configurablepistons.blocks.pistons.basePiston.BasicPistonBlock;
import ca.fxco.configurablepistons.pistonLogic.StickyType;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public class VeryStickyPistonBlock extends BasicPistonBlock implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {

    public VeryStickyPistonBlock() {
        super(true);
    }

    // I want to create a diagonal block entity instead of just teleporting blocks
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved || !state.get(EXTENDED)) return;
        BlockPos offsetPos = pos.offset(state.get(FACING));
        BlockPos offsetPos2 = offsetPos.offset(state.get(FACING));
        BlockState blockState = world.getBlockState(offsetPos2);
        if (blockState.getBlock() == this.getHeadBlock()) {
            world.setBlockState(offsetPos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
            world.setBlockState(offsetPos2, Blocks.AIR.getDefaultState(),
                    Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
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

    // Returns a list of directions that are sticky, and the stickyType.
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return Map.of(state.get(FACING),StickyType.STICKY); // Sticky Front
    }

    public StickyType sideStickiness(BlockState state, Direction direction) {
        return state.get(FACING) == direction ? StickyType.STICKY : StickyType.DEFAULT;
    }
}
