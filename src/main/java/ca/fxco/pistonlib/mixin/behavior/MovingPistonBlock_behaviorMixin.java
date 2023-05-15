package ca.fxco.pistonlib.mixin.behavior;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MovingPistonBlock.class)
public class MovingPistonBlock_behaviorMixin implements ConfigurablePistonBehavior {


    @Override
    public boolean canChangePistonMoveBehaviorOverride() {
        return false;
    }
}
