package ca.fxco.api.pistonlib.block;

import java.util.function.BooleanSupplier;

import ca.fxco.api.pistonlib.toggle.Toggleable;
import ca.fxco.api.pistonlib.toggle.ToggleableProperties;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * This interface collects all custom behavior that is injected into {@code BlockBehaviour.Properties}s.
 * It provides dummy implementations for all custom behavior. The actual implementations
 * of these methods are provided in their respective Mixin classes.
 */
public interface PLBlockProperties extends Toggleable, ToggleableProperties<Properties> {


    // Toggleable

    @Override
    default BooleanSupplier pl$getIsDisabled() {
        return null;
    }


    // ToggleableProperties

    @Override
    default Properties pl$setDisabled(BooleanSupplier isDisabled) {
        return null;
    }
}
