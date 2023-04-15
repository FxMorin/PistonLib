package ca.fxco.pistonlib.mixin.blockEntity;

import ca.fxco.pistonlib.impl.BlockEntityPostLoad;
import ca.fxco.pistonlib.impl.LevelAdditions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class Level_blockEntityMixin implements LevelAdditions {

    @Override
    public void addBlockEntityPostLoad(BlockEntityPostLoad blockEntity) {}
}
