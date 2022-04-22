package ca.fxco.configurablepistons.mixin;

import ca.fxco.configurablepistons.ConfigurablePistons;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.data.server.BlockTagProvider;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTagProvider.class)
public abstract class BlockTagProvider_registerMixin extends AbstractTagProvider<Block> {

    protected BlockTagProvider_registerMixin(DataGenerator root, Registry<Block> registry) {
        super(root, registry);
    }


    @Inject(
            method = "configure()V",
            at = @At("TAIL")
    )
    protected void configure(CallbackInfo ci) {
        this.getOrCreateTagBuilder(ConfigurablePistons.PISTONS).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                ConfigurablePistons.BASIC_PISTON,
                ConfigurablePistons.BASIC_STICKY_PISTON
        );
        this.getOrCreateTagBuilder(ConfigurablePistons.UNPUSHABLE).add(
                Blocks.OBSIDIAN,
                Blocks.CRYING_OBSIDIAN,
                Blocks.RESPAWN_ANCHOR
        );
    }
}
