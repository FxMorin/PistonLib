package ca.fxco.pistonlib.mixin.blockEntity;

import ca.fxco.api.pistonlib.blockEntity.BlockEntityPostLoad;
import ca.fxco.api.pistonlib.level.LevelAdditions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class Level_blockEntityMixin implements LevelAdditions {

    @Override
    public void addBlockEntityPostLoad(BlockEntityPostLoad blockEntity) {}
}
