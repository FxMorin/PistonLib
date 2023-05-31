package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.api.pistonlib.toggle.Toggleable;
import ca.fxco.api.pistonlib.toggle.ToggleableProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.BooleanSupplier;

@Mixin(Item.Properties.class)
public class Properties_itemMixin implements ToggleableProperties<Item.Properties>, Toggleable {

    @Unique
    BooleanSupplier isDisabled = () -> false;

    @Override
    public Item.Properties pl$setDisabled(BooleanSupplier isDisabled) {
        this.isDisabled = isDisabled;
        return (Item.Properties)(Object)this;
    }

    @Override
    public BooleanSupplier pl$getIsDisabled() {
        return this.isDisabled;
    }
}
