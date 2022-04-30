package ca.fxco.configurablepistons.helpers;

import ca.fxco.configurablepistons.base.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.EXTENDED;
import static net.minecraft.util.math.Direction.*;

public class PistonUtils {

    public static boolean isMovable(BlockState state, World wo, BlockPos pos,
                                    Direction dir, boolean canBreak, Direction pistonDir) {
        if (pos.getY() >= wo.getBottomY() && pos.getY() <= wo.getTopY() - 1 && wo.getWorldBorder().contains(pos)) {
            if (state.isAir()) return true;
            if (dir == DOWN && pos.getY() == wo.getBottomY()) return false;
            if (dir == UP && pos.getY() == wo.getTopY() - 1) return false;
            ConfigurablePistonBehavior customBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (customBehavior.usesConfigurablePistonBehavior()) { // This is where stuff gets fun
                if (customBehavior.isMovable(state))
                    return dir != pistonDir ? customBehavior.canPistonPull(state) :
                            customBehavior.canPistonPush(state) && (!customBehavior.canDestroy(state) || canBreak);
            } else {
                if (state.isIn(ModTags.UNPUSHABLE) || state.getHardness(wo, pos) == -1.0F) return false;
                if (state.isIn(ModTags.PISTONS)) return !state.get(EXTENDED) && !state.hasBlockEntity();
                return switch (state.getPistonBehavior()) {
                    case BLOCK -> false;
                    case DESTROY -> canBreak;
                    case PUSH_ONLY -> dir == pistonDir;
                    default -> !state.hasBlockEntity();
                };
            }
        }
        return false;
    }
}
