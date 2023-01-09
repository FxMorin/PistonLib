package ca.fxco.configurablepistons;

import ca.fxco.configurablepistons.base.ModBlockEntities;
import ca.fxco.configurablepistons.base.ModBlocks;
import ca.fxco.configurablepistons.renderers.BasicPistonBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ConfigurablePistonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlockEntities.BASIC_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CONFIGURABLE_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.LONG_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SPEED_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FAST_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.STICKY_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.TRANSLOCATION_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.SLIPPERY_MOVING_BLOCK_ENTITY, BasicPistonBlockEntityRenderer::new);

        BlockRenderLayerMap renderLayers = BlockRenderLayerMap.INSTANCE;
        renderLayers.putBlock(ModBlocks.HALF_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.HALF_HONEY_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIMY_REDSTONE_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.SLIPPERY_SLIME_BLOCK, RenderType.translucent());
        renderLayers.putBlock(ModBlocks.STICKY_CHAIN_BLOCK, RenderType.cutoutMipped());
    }
}
