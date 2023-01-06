package ca.fxco.configurablepistons.mixin;

import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.configurablepistons.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public class Block_pistonBehaviorMixin implements ConfigurablePistonBehavior, ConfigurablePistonStickiness {}
