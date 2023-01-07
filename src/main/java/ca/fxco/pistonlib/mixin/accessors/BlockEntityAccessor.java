package ca.fxco.pistonlib.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {

    @Accessor("type") @Mutable
    void setType(BlockEntityType<?> type);

}
