package ca.fxco.configurablepistons.helpers;

import ca.fxco.configurablepistons.Registerer;
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
            if ((dir == DOWN && pos.getY() == wo.getBottomY()) || (dir == UP && pos.getY() == wo.getTopY() - 1))
                return false; // Make sure it's a valid push like normally
            ConfigurablePistonBehavior customBehavior = (ConfigurablePistonBehavior)state.getBlock();
            if (customBehavior.usesConfigurablePistonBehavior(state)) { // This is where stuff gets fun
                if (customBehavior.isMovable(state)) {
                    if (dir == pistonDir) { // Is Pushing
                        if (!wo.isClient) {
                            System.out.println("Pushing Block - Dir: " + dir + " - pistonDir: " + pistonDir);
                            //Arrays.asList(Thread.currentThread().getStackTrace()).forEach(System.out::println);
                        }
                        return customBehavior.canPistonPush(state) && (!customBehavior.canDestroy(state) || canBreak);
                    } else {                // Is Pulling
                        if (!wo.isClient) {
                            System.out.println("Pulling Block");
                        }
                        return customBehavior.canPistonPull(state);
                    }
                }
            } else {
                if (!state.isIn(Registerer.UNPUSHABLE)) {
                    if (!state.isIn(Registerer.PISTONS)) {
                        if (state.getHardness(wo, pos) == -1.0F)
                            return false;
                        switch (state.getPistonBehavior()) {
                            case BLOCK:
                                return false;
                            case DESTROY:
                                return canBreak;
                            case PUSH_ONLY:
                                return dir == pistonDir;
                        }
                    } else if (state.get(EXTENDED)) {
                        return false;
                    }
                    return !state.hasBlockEntity();
                }
            }
        }
        return false;
    }
}
