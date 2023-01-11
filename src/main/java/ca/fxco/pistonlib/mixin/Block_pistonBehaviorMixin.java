package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonMerging;
import org.spongepowered.asm.mixin.Mixin;

import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonBehavior;
import ca.fxco.pistonlib.pistonLogic.accessible.ConfigurablePistonStickiness;

import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public class Block_pistonBehaviorMixin implements ConfigurablePistonBehavior, ConfigurablePistonStickiness, ConfigurablePistonMerging {}
