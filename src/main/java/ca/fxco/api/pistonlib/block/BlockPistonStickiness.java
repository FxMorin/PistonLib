package ca.fxco.api.pistonlib.block;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import ca.fxco.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.pistonlib.pistonLogic.sticky.StickyType;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockPistonStickiness {

    // Defines if this block can stick to the adjacent block. Only use this on sticky blocks
    @Nullable StickyGroup pl$getStickyGroup();

    boolean pl$hasStickyGroup();

    /*
     * These methods are only used if `usesConfigurablePistonStickiness` returns true
     * This allows for more configurable & conditional piston stickiness
     */

    // This must return true in order for the configurable piston stickiness to be used!
    boolean pl$usesConfigurablePistonStickiness() ;

    // If the block is currently sticky for any side, for quick checks to boost performance by
    // skipping more intensive checks early. For some checks it might just be faster to set this to true!
    boolean pl$isSticky(BlockState state);

    // Returns a list of directions that are sticky, and the stickyType.
    Map<Direction, StickyType> pl$stickySides(BlockState state);

    StickyType pl$sideStickiness(BlockState state, Direction dir);

    /**
     * This only gets used if the sticky type is {@linkplain ca.fxco.pistonlib.pistonLogic.StickyType#CONDITIONAL CONDITIONAL}.
     */
    boolean pl$matchesStickyConditions(BlockState state, BlockState neighborState, Direction dir);

}
