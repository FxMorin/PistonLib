package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.renderers.BasicPistonBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class ConfigurablePistonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Stop using the deprecated method!
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.BASIC_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.SPEED_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.FAST_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntities.STICKY_PISTON_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
    }
}
