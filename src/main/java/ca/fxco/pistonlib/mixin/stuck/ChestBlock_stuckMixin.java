package ca.fxco.pistonlib.mixin.stuck;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.world.level.block.ChestBlock.*;

@Mixin(ChestBlock.class)
public abstract class ChestBlock_stuckMixin implements ConfigurablePistonStickiness {

    private static Optional<Direction> getStickyDirection(BlockState state) {
        ChestType type = state.getValue(TYPE);
        if (type == ChestType.SINGLE) {
            return Optional.empty();
        }
        Direction dir = state.getValue(FACING);
        return Optional.of(type == ChestType.LEFT ? dir.getClockWise() : dir.getCounterClockWise());
    }

    @Override
    public boolean usesConfigurablePistonStickiness() {
        return PistonLibConfig.stuckDoubleBlocks;
    }

    @Override
    public Map<Direction, StickyType> stickySides(BlockState state) {
        return getStickyDirection(state).map(dir -> Map.of(dir, StickyType.CONDITIONAL)).orElseGet(Map::of);
    }

    @Override
    public StickyType sideStickiness(BlockState state, Direction dir) {
        Optional<Direction> dirOpt = getStickyDirection(state);
        return dirOpt.isPresent() && dirOpt.get() == dir ? StickyType.CONDITIONAL : StickyType.DEFAULT;
    }

    @Override
    public boolean matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir) {
        if (state.is(neighborState.getBlock())) {
            Optional<Direction> dirOpt = getStickyDirection(neighborState);
            return dirOpt.isPresent() && dirOpt.get() == dir.getOpposite();
        }
        return false;
    }
}
