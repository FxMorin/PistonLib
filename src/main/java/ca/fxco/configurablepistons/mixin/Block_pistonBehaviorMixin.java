package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class Block_pistonBehaviorMixin implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {}
